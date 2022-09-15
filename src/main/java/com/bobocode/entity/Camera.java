package com.bobocode.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cameras")
@Data
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer nasaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "camera", cascade = CascadeType.PERSIST)
    private List<Picture> pictures = new ArrayList<>();

    public void addPicture(Picture picture) {
        picture.setCamera(this);
        pictures.add(picture);
    }
}
