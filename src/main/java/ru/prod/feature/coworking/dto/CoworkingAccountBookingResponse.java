package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingAccountBookingResponse {

    @NotNull
    private String login;

    @NotNull
    private LocalDateTime from;

    @NotNull
    private LocalDateTime to;
}
