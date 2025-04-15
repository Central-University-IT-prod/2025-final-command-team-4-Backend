package ru.prod.feature.coworking.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.prod.entity.*;
import ru.prod.feature.account.service.AccountService;
import ru.prod.feature.coworking.dto.*;
import ru.prod.feature.coworking.exception.CoworkingNotFoundException;
import ru.prod.feature.coworking.repository.CoworkingAccountRepository;
import ru.prod.feature.coworking.repository.CoworkingBookRepository;
import ru.prod.feature.coworking.repository.CoworkingPlaceBookRepository;
import ru.prod.feature.coworking.repository.CoworkingRoomBookRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
@Service
@RequiredArgsConstructor
public class CoworkingBookService {

    private final CoworkingBookRepository coworkingBookRepository;

    private final CoworkingRoomBookRepository coworkingRoomBookRepository;

    private final CoworkingPlaceBookRepository coworkingPlaceBookRepository;

    private final CoworkingAccountRepository coworkingAccountRepository;

    private final AccountService accountService;

    public CoworkingSpaceFloorRoomBook findRoomBookById(UUID roomBookId) {
        return coworkingRoomBookRepository.findById(roomBookId)
                .orElseThrow(() -> new CoworkingNotFoundException("Coworking room book with id " + roomBookId + " not found"));
    }

    public CoworkingSpaceFloorPlaceBook findPlaceBookById(UUID placeBookId) {
        return coworkingPlaceBookRepository.findById(placeBookId)
                .orElseThrow(() -> new CoworkingNotFoundException("Coworking place book with id " + placeBookId + " not found"));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CoworkingBookResponse bookPlace(UUID accountId,
                                           UUID coworkingId,
                                           UUID placeId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        CoworkingSpaceFloorPlace place = coworkingBookRepository.findByCoworkingIdAndPlaceId(coworkingId, placeId)
                .orElseThrow(NoSuchElementException::new);

        if (!isAllowedPlaceToBook(placeId, from, to))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot be booked");

        CoworkingSpaceFloorPlaceBook book = new CoworkingSpaceFloorPlaceBook();
        book.setBookingAccount(coworkingAccountRepository.getByAccountId(accountId));
        book.setCoworkingSpaceFloorPlace(place);
        book.setStartTime(from);
        book.setEndTime(to);
        CoworkingSpaceFloorPlaceBook savedBook = coworkingBookRepository.save(book);

        return new CoworkingBookResponse(
            savedBook.getId(),
            savedBook.getValidation().toString()
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CoworkingBookResponse bookRoom(UUID accountId,
                                           UUID coworkingId,
                                           UUID roomId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        CoworkingSpaceFloorRoom room = coworkingBookRepository.findByCoworkingIdAndRoomId(coworkingId, roomId)
                .orElseThrow(NoSuchElementException::new);

        if (!isAllowedRoomToBook(roomId, from, to))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot be booked");

        CoworkingSpaceFloorRoomBook book = new CoworkingSpaceFloorRoomBook();
        book.setBookingAccount(coworkingAccountRepository.getByAccountId(accountId));
        book.setCoworkingSpaceFloorRoom(room);
        book.setStartTime(from);
        book.setEndTime(to);
        CoworkingSpaceFloorRoomBook savedBook = coworkingRoomBookRepository.save(book);

        return new CoworkingBookResponse(
            savedBook.getId(),
            savedBook.getValidation().toString()
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CoworkingBookResponse putBookPlace(UUID accountId,
                                           UUID bookId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        CoworkingSpaceFloorPlaceBook book = coworkingBookRepository
                .findBookByBookId(bookId)
                .orElseThrow(NoSuchElementException::new);
        Account account = accountService.findById(accountId);

        if (!book.getBookingAccount().getId().equals(accountId) && !account.getIsAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if (!isAllowedPlaceToBook(bookId, from, to, book)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot be booked");
        }

        book.setStartTime(from);
        book.setEndTime(to);
        CoworkingSpaceFloorPlaceBook savedBook = coworkingBookRepository.save(book);

        return new CoworkingBookResponse(
            savedBook.getId(),
            savedBook.getValidation().toString()
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CoworkingBookResponse putBookRoom(UUID accountId,
                                           UUID bookId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        CoworkingSpaceFloorRoomBook room = coworkingRoomBookRepository
                .findBookByBookId(bookId)
                .orElseThrow(NoSuchElementException::new);

        if (!room.getBookingAccount().getId().equals(accountId) && !room.getBookingAccount().getIsAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        if (!isAllowedRoomToBook(bookId, from, to, room))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot be booked");

        room.setStartTime(from);
        room.setEndTime(to);
        CoworkingSpaceFloorRoomBook savedBook = coworkingRoomBookRepository.save(room);

        return new CoworkingBookResponse(
            savedBook.getId(),
            savedBook.getValidation().toString()
        );
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteBookPlace(UUID accountId, UUID bookId) {
        CoworkingSpaceFloorPlaceBook book = coworkingBookRepository
                .findBookByBookId(bookId)
                .orElseThrow(NoSuchElementException::new);
        Account account = accountService.findById(accountId);

        if (!book.getBookingAccount().getId().equals(accountId) && !account.getIsAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        coworkingBookRepository.delete(book);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteBookRoom(UUID accountId, UUID bookId) {
        CoworkingSpaceFloorRoomBook room = coworkingRoomBookRepository
                .findBookByBookId(bookId)
                .orElseThrow(NoSuchElementException::new);

        if (!room.getBookingAccount().getId().equals(accountId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        coworkingRoomBookRepository.delete(room);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    protected boolean isAllowedPlaceToBook(UUID bookId,
                                           LocalDateTime from,
                                           LocalDateTime to,
                                           CoworkingSpaceFloorPlaceBook... ignore) {
        List<CoworkingSpaceFloorPlaceBook> books = coworkingBookRepository.findBookByBookId(
                bookId,
                from,
                to
        );

        for (CoworkingSpaceFloorPlaceBook book : ignore)
            books.remove(book);

        return books.isEmpty();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = true)
    protected boolean isAllowedRoomToBook(UUID bookId,
                                          LocalDateTime from,
                                          LocalDateTime to,
                                          CoworkingSpaceFloorRoomBook... ignore) {
        List<CoworkingSpaceFloorPlaceBook> places = coworkingRoomBookRepository.findBooksByCoworkingIdAndBookId(
                bookId,
                from,
                to
        );

        List<CoworkingSpaceFloorRoomBook> rooms = coworkingRoomBookRepository.findRoomBooksByCoworkingIdAndRoomId(
                bookId,
                from,
                to
        );

        for (CoworkingSpaceFloorRoomBook book : ignore)
            rooms.remove(book);

        return rooms.isEmpty() && places.isEmpty();
    }

    public CoworkingFloorPlaceResponse verifyPlaceBook(UUID accountId, UUID placeBookId) {
        isAdmin(accountId);

        CoworkingSpaceFloorPlaceBook placeBook = findPlaceBookById(placeBookId);

        return new CoworkingFloorPlaceResponse(
                placeBook.getCoworkingSpaceFloorPlace().getId(),
                placeBook.getCoworkingSpaceFloorPlace().getLatitude(),
                placeBook.getCoworkingSpaceFloorPlace().getLongitude(),
                placeBook.getCoworkingSpaceFloorPlace().getPrice(),
                List.of(
                        new CoworkingAccountBookingResponse(
                                placeBook.getBookingAccount().getLogin(),
                                placeBook.getStartTime(),
                                placeBook.getEndTime()
                        )
                )
        );
    }

    private void isAdmin(UUID accountId) {
        Account account = accountService.findById(accountId);

        if (!account.getIsAdmin()) {
            throw new AccessDeniedException("You don't have permission to get accounts");
        }
    }

    public CoworkingFloorRoomResponse verifyRoomBook(UUID accountId, UUID roomBookId) {
        isAdmin(accountId);

        CoworkingSpaceFloorRoomBook roomBook = findRoomBookById(roomBookId);

        return new CoworkingFloorRoomResponse(
                roomBookId,
                roomBook.getCoworkingSpaceFloorRoom().getLatitude(),
                roomBook.getCoworkingSpaceFloorRoom().getLongitude(),
                roomBook.getCoworkingSpaceFloorRoom().getPrice(),
                List.of(
                        new CoworkingAccountBookingResponse(
                                roomBook.getBookingAccount().getLogin(),
                                roomBook.getStartTime(),
                                roomBook.getEndTime()
                        )
                )
        );
    }

    public List<CoworkingAccountBookResponse> getActiveAccountBookings(UUID accountId, int page, int size) {
        Account account = accountService.findById(accountId);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));

        Page<CoworkingAccountBookResponse> placeBookings = coworkingBookRepository.findActivePlaceBookingsByAccount(account.getId(), pageable);
        Page<CoworkingAccountBookResponse> roomBookings = coworkingBookRepository.findActiveRoomBookingsByAccount(account.getId(), pageable);

        return getBookingsResponse(pageable, placeBookings, roomBookings);
    }

    public List<CoworkingAccountBookResponse> getAccountsBookings(UUID accountId, int size, int page) {
        isAdmin(accountId);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));

        Page<CoworkingAccountBookResponse> placeBookings = coworkingBookRepository.findAllPlaceBookings(pageable);
        Page<CoworkingAccountBookResponse> roomBookings = coworkingBookRepository.findAllRoomBookings(pageable);

        return getBookingsResponse(pageable, placeBookings, roomBookings);
    }

    @NotNull
    private List<CoworkingAccountBookResponse> getBookingsResponse(PageRequest pageable, Page<CoworkingAccountBookResponse> placeBookings, Page<CoworkingAccountBookResponse> roomBookings) {
        List<CoworkingAccountBookResponse> allBookings = new ArrayList<>();
        allBookings.addAll(placeBookings.getContent());
        allBookings.addAll(roomBookings.getContent());

        int total = (int) (placeBookings.getTotalElements() + roomBookings.getTotalElements());
        return new PageImpl<>(allBookings, pageable, total).toList();
    }

    public List<CoworkingAccountBookResponse> getAccountHistoryBookings(UUID accountId, int size, int page) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CoworkingAccountBookResponse> placeBookings = coworkingBookRepository.findAccountPlaceBookings(accountId, pageable);
        Page<CoworkingAccountBookResponse> roomBookings = coworkingBookRepository.findAccountRoomBookings(accountId, pageable);

        return getBookingsResponse(pageable, placeBookings, roomBookings);
    }
}
