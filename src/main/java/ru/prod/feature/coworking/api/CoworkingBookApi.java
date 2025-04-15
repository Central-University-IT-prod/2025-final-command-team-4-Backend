package ru.prod.feature.coworking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.prod.feature.coworking.dto.CoworkingBookResponse;
import ru.prod.feature.coworking.dto.CoworkingFloorPlaceResponse;
import ru.prod.feature.coworking.dto.CoworkingFloorRoomResponse;
import ru.prod.feature.coworking.dto.CoworkingAccountBookResponse;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Tag(name = "Book")
@RequestMapping("/api/v1")
public interface CoworkingBookApi {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get-active-campaigns")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/coworking/books/active")
    List<CoworkingAccountBookResponse> getActiveAccountBookings(
            @RequestAttribute("accountId") UUID accountId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-place")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/coworking/{coworkingId}/place/{placeId}")
    CoworkingBookResponse createBookPlace(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("coworkingId") UUID coworkingId,
            @PathVariable("placeId") UUID placeId,
            @RequestParam("startAt") ZonedDateTime startAt,
            @RequestParam("endAt") ZonedDateTime endAt);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/coworking/{coworkingId}/room/{roomId}")
    CoworkingBookResponse createBookRoom(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("coworkingId") UUID coworkingId,
            @PathVariable("roomId") UUID roomId,
            @RequestParam("startAt") ZonedDateTime startAt,
            @RequestParam("endAt") ZonedDateTime endAt);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-room-edit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/room/{bookId}")
    CoworkingBookResponse putBookRoom(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("bookId") UUID bookId,
            @RequestParam("startAt") ZonedDateTime startAt,
            @RequestParam("endAt") ZonedDateTime endAt);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-place-edit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "409")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/place/{bookId}")
    CoworkingBookResponse putBookPlace(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("bookId") UUID bookId,
            @RequestParam("startAt") ZonedDateTime startAt,
            @RequestParam("endAt") ZonedDateTime endAt);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-delete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/room/{bookId}")
    void deleteBookRoom(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("bookId") UUID bookId);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "book-delete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "404")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/place/{bookId}")
    void deleteBookPlace(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable("bookId") UUID bookId);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "verify-place-book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "403")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/coworking/verify/place/{placeBookId}")
    CoworkingFloorPlaceResponse verifyPlaceBook(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable UUID placeBookId
    );

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "verify-room-book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404"),
            @ApiResponse(responseCode = "403")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/coworking/verify/room/{roomBookId}")
    CoworkingFloorRoomResponse verifyRoomBook(
            @RequestAttribute("accountId") UUID accountId,
            @PathVariable UUID roomBookId
    );

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get-all-bookings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/coworking/books")
    List<CoworkingAccountBookResponse> getAllBookings(
            @RequestAttribute UUID accountId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    );

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "get-history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/books/history")
    List<CoworkingAccountBookResponse> getAccountHistory(
            @RequestAttribute UUID accountId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    );
}
