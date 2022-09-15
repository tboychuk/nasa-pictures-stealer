package com.bobocode.repository;

import com.bobocode.entity.Camera;
import com.bobocode.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CameraRepository extends JpaRepository<Camera, Long> {
    Optional<Camera> findByNasaId(Integer cameraNasaId);
}
