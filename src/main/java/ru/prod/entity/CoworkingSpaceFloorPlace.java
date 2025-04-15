package ru.prod.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "coworking_space_floor_place")
public class CoworkingSpaceFloorPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "coworking_space_floor_id", nullable = false)
    private CoworkingSpaceFloor coworkingSpaceFloor;

    @ManyToOne
    @JoinColumn(name = "coworking_space_floor_room_id")
    private CoworkingSpaceFloorRoom coworkingSpaceFloorRoom;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
