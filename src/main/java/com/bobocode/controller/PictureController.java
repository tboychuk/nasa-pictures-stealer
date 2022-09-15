package com.bobocode.controller;

import com.bobocode.controller.dto.PictureRequestDto;
import com.bobocode.service.PictureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/pictures")
@RequiredArgsConstructor
public class PictureController {
    private final PictureService pictureService;

    @PostMapping
    public void stealPictures(@RequestBody PictureRequestDto requestDto) {
        log.info("Received request to steal picture by sol = '{}'", requestDto.sol());
        pictureService.stealPicturesBySol(requestDto.sol());
    }

    @GetMapping(value = "/largest", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getLargestPicture() {
        return pictureService.loadLargestPicture();
    }
}
