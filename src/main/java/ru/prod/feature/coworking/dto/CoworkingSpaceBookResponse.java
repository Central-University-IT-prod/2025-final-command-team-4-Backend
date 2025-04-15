package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingSpaceBookResponse {

    @NotNull
    private UUID id;

    @NotNull
    private String location;

    @NotNull
    private String title;

    @NotNull
    private String time;

    @NotNull
    private String description;
}
