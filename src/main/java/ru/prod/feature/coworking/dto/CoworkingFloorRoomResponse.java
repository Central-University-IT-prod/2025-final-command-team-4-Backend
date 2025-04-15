package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingFloorRoomResponse {

    @NotNull
    private UUID roomId;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private BigDecimal price;

    @NotNull
    private List<CoworkingAccountBookingResponse> bookings;
}
