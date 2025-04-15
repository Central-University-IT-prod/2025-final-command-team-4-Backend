package ru.prod.feature.coworking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.prod.feature.coworking.api.CoworkingBookApi;
import ru.prod.feature.coworking.dto.CoworkingAccountBookResponse;
import ru.prod.feature.coworking.dto.CoworkingBookResponse;
import ru.prod.feature.coworking.dto.CoworkingFloorPlaceResponse;
import ru.prod.feature.coworking.dto.CoworkingFloorRoomResponse;
import ru.prod.feature.coworking.service.CoworkingBookService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CoworkingBookController implements CoworkingBookApi {

    private final CoworkingBookService coworkingBookService;

    @Override
    public List<CoworkingAccountBookResponse> getActiveAccountBookings(UUID accountId, Integer page, Integer limit) {
        return coworkingBookService.getActiveAccountBookings(accountId, page, limit);
    }

    @Override
    public CoworkingBookResponse createBookPlace(UUID accountId, UUID coworkingId, UUID placeId, ZonedDateTime startAt, ZonedDateTime endAt) {
        if (startAt.isAfter(endAt) || startAt.isEqual(endAt))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return coworkingBookService.bookPlace(accountId, coworkingId, placeId, startAt.toLocalDateTime(), endAt.toLocalDateTime());
    }

    @Override
    public CoworkingBookResponse createBookRoom(UUID accountId, UUID coworkingId, UUID roomId, ZonedDateTime startAt, ZonedDateTime endAt) {
        if (startAt.isAfter(endAt) || startAt.isEqual(endAt))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return coworkingBookService.bookRoom(accountId, coworkingId, roomId, startAt.toLocalDateTime(), endAt.toLocalDateTime());
    }

    @Override
    public CoworkingBookResponse putBookRoom(UUID accountId, UUID bookId, ZonedDateTime startAt, ZonedDateTime endAt) {
        if (startAt.isAfter(endAt) || startAt.isEqual(endAt))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return coworkingBookService.putBookRoom(accountId, bookId, startAt.toLocalDateTime(), endAt.toLocalDateTime());
    }

    @Override
    public CoworkingBookResponse putBookPlace(UUID accountId, UUID bookId, ZonedDateTime startAt, ZonedDateTime endAt) {
        if (startAt.isAfter(endAt) || startAt.isEqual(endAt))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return coworkingBookService.putBookPlace(accountId, bookId, startAt.toLocalDateTime(), endAt.toLocalDateTime());
    }

    @Override
    public void deleteBookRoom(UUID accountId, UUID bookId) {
        coworkingBookService.deleteBookRoom(accountId, bookId);
    }

    @Override
    public void deleteBookPlace(UUID accountId, UUID bookId) {
        coworkingBookService.deleteBookPlace(accountId, bookId);
    }

    @Override
    public CoworkingFloorPlaceResponse verifyPlaceBook(UUID accountId, UUID placeBookId) {
        return coworkingBookService.verifyPlaceBook(accountId, placeBookId);
    }

    @Override
    public CoworkingFloorRoomResponse verifyRoomBook(UUID accountId, UUID roomBookId) {
        return coworkingBookService.verifyRoomBook(accountId, roomBookId);
    }

    @Override
    public List<CoworkingAccountBookResponse> getAllBookings(UUID accountId, Integer page, Integer size) {
        return coworkingBookService.getAccountsBookings(accountId, size, page);
    }

    @Override
    public List<CoworkingAccountBookResponse> getAccountHistory(UUID accountId, Integer page, Integer size) {
        return coworkingBookService.getAccountHistoryBookings(accountId, size, page);
    }
}
