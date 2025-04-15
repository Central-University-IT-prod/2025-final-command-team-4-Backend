package ru.prod.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "coworking_space_image")
public class CoworkingSpaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "coworking_space_id")
    private CoworkingSpace coworkingSpace;

    @Column(name = "object")
    private String object;

    @Column(name = "image_order")
    private Integer imageOrder;
}
