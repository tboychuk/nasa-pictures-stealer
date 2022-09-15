package com.bobocode.service;

import com.bobocode.entity.Camera;
import com.bobocode.entity.Picture;
import com.bobocode.repository.CameraRepository;
import com.bobocode.repository.PictureRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;

@Log4j2
@Service
@RequiredArgsConstructor
public class PictureService {
    private final PictureRepository pictureRepository;
    private final CameraRepository cameraRepository;
    private final RestTemplate restTemplate;
    @Value("${nasa.api.url}")
    private String nasaApiUrl;
    @Value("${nasa.api.key}")
    private String nasaApiKey;

    @Transactional
    public void stealPicturesBySol(int sol) {
        var uri = buildPicturesUri(sol);
        log.info("Calling NASA: {}", uri);
        var jsonResponse = restTemplate.getForObject(uri, JsonNode.class);
        processNasaResponse(jsonResponse);
    }

    private void processNasaResponse(JsonNode jsonResponse) {
        log.info("Processing NASA response: '{}'", jsonResponse);
        for (var pictureJson : jsonResponse.get("photos")) {
            var camera = resolveCamera(pictureJson.get("camera"));
            var picture = resolvePicture(pictureJson);
            camera.addPicture(picture);
        }
    }

    private Camera resolveCamera(JsonNode cameraJson) {
        var cameraNasaId = cameraJson.get("id").asInt();
        return cameraRepository.findByNasaId(cameraNasaId)
                .orElseGet(() -> storeNewCamera(cameraJson));
    }

    private Camera storeNewCamera(JsonNode cameraJson) {
        var camera = new Camera();
        camera.setNasaId(cameraJson.get("id").asInt());
        camera.setName(cameraJson.get("name").asText());
        return cameraRepository.save(camera);
    }

    private Picture resolvePicture(JsonNode pictureJson) {
        return pictureRepository.findByNasaId(pictureJson.get("id").asLong())
                .orElseGet(() -> createPicture(pictureJson));
    }

    private Picture createPicture(JsonNode pictureJson) {
        var picture = new Picture();
        picture.setNasaId(pictureJson.get("id").asLong());
        picture.setImgSrc(pictureJson.get("img_src").asText());
        return picture;
    }

    private String buildPicturesUri(int sol) {
        return UriComponentsBuilder.fromHttpUrl(nasaApiUrl)
                .queryParam("api_key", nasaApiKey)
                .queryParam("sol", sol)
                .toUriString();
    }

    public byte[] loadLargestPicture() {
        var localPictures = pictureRepository.findAll();
        var largestPicture = localPictures.parallelStream()
                .map(picture -> loadSize(picture))
                .max(Comparator.comparing(PictureDto::size))
                .orElseThrow();
        return restTemplate.getForObject(largestPicture.imageLocation, byte[].class);
    }

    private PictureDto loadSize(Picture picture) {
        var initialHeaders = restTemplate.headForHeaders(picture.getImgSrc());
        URI imageLocation = initialHeaders.getLocation();
        var redirectHeaders = restTemplate.headForHeaders(imageLocation);
        return new PictureDto(imageLocation, redirectHeaders.getContentLength());
    }

    record PictureDto(URI imageLocation, long size) {
    }
    
}
