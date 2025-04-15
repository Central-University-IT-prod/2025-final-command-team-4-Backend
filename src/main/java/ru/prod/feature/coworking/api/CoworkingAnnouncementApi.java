package ru.prod.feature.coworking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementRequest;
import ru.prod.feature.coworking.dto.CoworkingAnnouncementResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "Announcement")
@RequestMapping("/api/v1/announcement")
public interface CoworkingAnnouncementApi {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "create-announcement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    CoworkingAnnouncementResponse create(
            @RequestAttribute("accountId") UUID accountId,
            @RequestBody @Valid CoworkingAnnouncementRequest request
    );  

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get-announcements")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true)
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<CoworkingAnnouncementResponse> getAnnouncements(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    );
}
