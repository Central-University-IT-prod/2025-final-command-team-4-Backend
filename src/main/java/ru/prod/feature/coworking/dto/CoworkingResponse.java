package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingResponse {

    @NotNull
    private String location;

    @NotNull
    private String title;

    @NotNull
    private String time;

    @NotNull
    private String description;

    @NotNull
    private List<String> imageUrls;

    @NotNull
    private List<CoworkingFloorResponse> floors;

    @NotNull
    private Integer freePlaces;
}
