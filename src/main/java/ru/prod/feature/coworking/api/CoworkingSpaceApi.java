package ru.prod.feature.coworking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.prod.feature.coworking.dto.CoworkingElementResponse;
import ru.prod.feature.coworking.dto.CoworkingResponse;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Coworking")
@RequestMapping("/api/v1/coworking")
public interface CoworkingSpaceApi {

    @Operation(summary = "find-all")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<CoworkingElementResponse> findAll();

    @Operation(summary = "find-by-id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{coworkingId}")
    CoworkingResponse findCoworkingSpace(
            @PathVariable("coworkingId") UUID coworkingId,
            @RequestParam("startAt") ZonedDateTime startAt,
            @RequestParam("endAt") ZonedDateTime endAt);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "upload-floor-image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("floor/{floorId}/image")
    void uploadFloorImage(
            @RequestAttribute("accountId") UUID accountId,
            @RequestParam("file") MultipartFile file,
            @PathVariable UUID floorId);

    @Operation(summary = "download-floor-image")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("floor/{floorId}/image")
    ResponseEntity<InputStreamResource> downloadFloorImage(
            @PathVariable UUID floorId
    );
}
