package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingElementResponse {

    @NotNull
    private UUID coworkingId;

    @NotNull
    private String title;

    @NotNull
    private String location;

    @NotNull
    private String description;

    @NotNull
    private Integer freePlaces;

    @NotNull
    private List<String> imageUrls;
}
