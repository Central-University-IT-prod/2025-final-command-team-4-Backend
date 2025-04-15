package ru.prod.feature.coworking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoworkingBookResponse {

    @NotNull
    private UUID bookId;

    @NotNull
    private String validationData;
}
