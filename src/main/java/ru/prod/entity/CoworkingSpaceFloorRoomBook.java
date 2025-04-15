package ru.prod.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "coworking_space_floor_room_book")
public class CoworkingSpaceFloorRoomBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "validation", nullable = false, updatable = false)
    private UUID validation = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "coworking_space_floor_room_id", nullable = false)
    private CoworkingSpaceFloorRoom coworkingSpaceFloorRoom;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account bookingAccount;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;
}
