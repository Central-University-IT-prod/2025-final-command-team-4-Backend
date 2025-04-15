package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.prod.feature.account.dto.AccountProfileResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingAccountBookResponse {

    @NotNull
    private UUID bookId;

    @NotNull
    private CoworkingSpaceBookResponse coworking;

    @NotNull
    private AccountProfileResponse profile;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Boolean isRoom;
}
