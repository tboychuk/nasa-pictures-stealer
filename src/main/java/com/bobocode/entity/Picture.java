package com.bobocode.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "imgSrc")
@Entity
@Table(name = "pictures")
public class Picture {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long nasaId;

    @Column(nullable = false, unique = true)
    private String imgSrc;

    @Column(nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "camera_id")
    private Camera camera;
}
