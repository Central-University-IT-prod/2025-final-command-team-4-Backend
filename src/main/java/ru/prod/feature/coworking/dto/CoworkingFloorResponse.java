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
public class CoworkingFloorResponse {

    @NotNull
    private UUID floorId;

    @NotNull
    private List<CoworkingFloorPlaceResponse> places;

    @NotNull
    private List<CoworkingFloorRoomResponse> rooms;

    @NotNull
    private Integer freePlaces;

    @NotNull
    private Integer floorOrder;
}
