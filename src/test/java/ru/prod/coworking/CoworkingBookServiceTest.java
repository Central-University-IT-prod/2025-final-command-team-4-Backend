package ru.prod.coworking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import ru.prod.entity.*;
import ru.prod.feature.account.service.AccountService;
import ru.prod.feature.coworking.exception.CoworkingNotFoundException;
import ru.prod.feature.coworking.repository.CoworkingAccountRepository;
import ru.prod.feature.coworking.repository.CoworkingBookRepository;
import ru.prod.feature.coworking.repository.CoworkingPlaceBookRepository;
import ru.prod.feature.coworking.repository.CoworkingRoomBookRepository;
import ru.prod.feature.coworking.service.CoworkingBookService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoworkingBookServiceTest {
    @Mock
    private CoworkingBookRepository coworkingBookRepository;

    @Mock
    private CoworkingRoomBookRepository coworkingRoomBookRepository;

    @Mock
    private CoworkingPlaceBookRepository coworkingPlaceBookRepository;

    @Mock
    private CoworkingAccountRepository coworkingAccountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private CoworkingBookService coworkingBookService;

    private UUID accountId;
    private UUID coworkingId;
    private UUID placeId;
    private UUID roomId;
    private LocalDateTime from;
    private LocalDateTime to;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        coworkingId = UUID.randomUUID();
        placeId = UUID.randomUUID();
        roomId = UUID.randomUUID();
        from = LocalDateTime.now();
        to = LocalDateTime.now().plusHours(1);
    }

    @Test
    void findRoomBookById_WhenRoomBookExists_ShouldReturnRoomBook() {
        var roomBook = new CoworkingSpaceFloorRoomBook();
        when(coworkingRoomBookRepository.findById(roomId)).thenReturn(Optional.of(roomBook));

        var result = coworkingBookService.findRoomBookById(roomId);

        assertNotNull(result);
        verify(coworkingRoomBookRepository, times(1)).findById(roomId);
    }

    @Test
    void findRoomBookById_WhenRoomBookNotFound_ShouldThrowException() {
        when(coworkingRoomBookRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThrows(CoworkingNotFoundException.class, () -> coworkingBookService.findRoomBookById(roomId));
    }

    @Test
    void findPlaceBookById_WhenPlaceBookExists_ShouldReturnPlaceBook() {
        var placeBook = new CoworkingSpaceFloorPlaceBook();
        when(coworkingPlaceBookRepository.findById(placeId)).thenReturn(Optional.of(placeBook));

        CoworkingSpaceFloorPlaceBook result = coworkingBookService.findPlaceBookById(placeId);

        assertNotNull(result);
        verify(coworkingPlaceBookRepository, times(1)).findById(placeId);
    }

    @Test
    void findPlaceBookById_WhenPlaceBookNotFound_ShouldThrowException() {
        when(coworkingPlaceBookRepository.findById(placeId)).thenReturn(Optional.empty());

        assertThrows(CoworkingNotFoundException.class, () -> coworkingBookService.findPlaceBookById(placeId));
        verify(coworkingPlaceBookRepository, times(1)).findById(placeId);
    }

    @Test
    void bookPlace_WhenPlaceIsAvailable_ShouldReturnBookResponse() {
        var place = new CoworkingSpaceFloorPlace();
        when(coworkingBookRepository.findByCoworkingIdAndPlaceId(coworkingId, placeId)).thenReturn(Optional.of(place));
        when(coworkingBookRepository.findBookByCoworkingIdAndPlaceId(placeId, from, to)).thenReturn(List.of());
        when(coworkingAccountRepository.getByAccountId(accountId)).thenReturn(new Account());
        when(coworkingBookRepository.save(any())).thenReturn(new CoworkingSpaceFloorPlaceBook());

        var result = coworkingBookService.bookPlace(accountId, coworkingId, placeId, from, to);

        assertNotNull(result);
        verify(coworkingBookRepository, times(1)).findByCoworkingIdAndPlaceId(coworkingId, placeId);
        verify(coworkingBookRepository, times(1)).save(any());
    }

    @Test
    void bookPlace_WhenPlaceIsNotAvailable_ShouldThrowConflictException() {
        CoworkingSpaceFloorPlace place = new CoworkingSpaceFloorPlace();
        when(coworkingBookRepository.findByCoworkingIdAndPlaceId(coworkingId, placeId)).thenReturn(Optional.of(place));
        when(coworkingBookRepository.findBookByCoworkingIdAndPlaceId(placeId, from, to)).thenReturn(List.of(new CoworkingSpaceFloorPlaceBook()));

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.bookPlace(accountId, coworkingId, placeId, from, to));
        verify(coworkingBookRepository, times(1)).findByCoworkingIdAndPlaceId(coworkingId, placeId);
    }

    @Test
    void bookRoom_WhenRoomIsAvailable_ShouldReturnBookResponse() {
        var room = new CoworkingSpaceFloorRoom();
        when(coworkingBookRepository.findByCoworkingIdAndRoomId(coworkingId, roomId)).thenReturn(Optional.of(room));
        when(coworkingRoomBookRepository.findBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of());
        when(coworkingRoomBookRepository.findRoomBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of());
        when(coworkingAccountRepository.getByAccountId(accountId)).thenReturn(new Account());
        when(coworkingRoomBookRepository.save(any())).thenReturn(new CoworkingSpaceFloorRoomBook());

        var result = coworkingBookService.bookRoom(accountId, coworkingId, roomId, from, to);

        assertNotNull(result);
        verify(coworkingBookRepository, times(1)).findByCoworkingIdAndRoomId(coworkingId, roomId);
        verify(coworkingRoomBookRepository, times(1)).save(any());
    }

    @Test
    void bookRoom_WhenRoomIsNotAvailable_ShouldThrowConflictException() {
        CoworkingSpaceFloorRoom room = new CoworkingSpaceFloorRoom();
        when(coworkingBookRepository.findByCoworkingIdAndRoomId(coworkingId, roomId)).thenReturn(Optional.of(room));
        when(coworkingRoomBookRepository.findBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of(new CoworkingSpaceFloorPlaceBook()));
        when(coworkingRoomBookRepository.findRoomBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of());

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.bookRoom(accountId, coworkingId, roomId, from, to));
        verify(coworkingBookRepository, times(1)).findByCoworkingIdAndRoomId(coworkingId, roomId);
    }

    @Test
    void putBookPlace_WhenUserIsAuthorized_ShouldUpdateBooking() {
        var book = new CoworkingSpaceFloorPlaceBook();
        var account = new Account();
        account.setId(accountId);
        book.setBookingAccount(account);
        when(coworkingBookRepository.findBookByCoworkingIdAndRoomId(coworkingId, placeId)).thenReturn(Optional.of(book));
        when(coworkingBookRepository.save(any())).thenReturn(book);

        var result = coworkingBookService.putBookPlace(accountId, placeId, from, to);

        assertNotNull(result);
        verify(coworkingBookRepository, times(1)).findBookByCoworkingIdAndRoomId(coworkingId, placeId);
        verify(coworkingBookRepository, times(1)).save(any());
    }

    @Test
    void putBookPlace_WhenUserIsNotAuthorized_ShouldThrowForbiddenException() {
        CoworkingSpaceFloorPlaceBook book = new CoworkingSpaceFloorPlaceBook();

        var account = new Account();
        account.setId(UUID.randomUUID());
        book.setBookingAccount(account);
        when(coworkingBookRepository.findBookByCoworkingIdAndRoomId(coworkingId, placeId)).thenReturn(Optional.of(book));

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.putBookPlace(accountId, placeId, from, to));
        verify(coworkingBookRepository, times(1)).findBookByCoworkingIdAndRoomId(coworkingId, placeId);
    }

    @Test
    void deleteBookPlace_WhenUserIsAuthorized_ShouldDeleteBooking() {
        var book = new CoworkingSpaceFloorPlaceBook();

        var account = new Account();
        account.setId(accountId);
        book.setBookingAccount(account);
        var bookId = UUID.randomUUID();

        when(coworkingBookRepository.findBookByBookId(bookId)).thenReturn(Optional.of(book));

        coworkingBookService.deleteBookPlace(accountId, bookId);

        verify(coworkingBookRepository, times(1)).findBookByBookId(bookId);
        verify(coworkingBookRepository, times(1)).delete(book);
    }

    @Test
    void deleteBookPlace_WhenUserIsNotAuthorized_ShouldThrowForbiddenException() {
        var book = new CoworkingSpaceFloorPlaceBook();

        var account = new Account();
        account.setId(UUID.randomUUID());
        account.setIsAdmin(false);
        book.setBookingAccount(account);

        var bookId = UUID.randomUUID();
        when(accountService.findById(accountId)).thenReturn(account);
        when(coworkingBookRepository.findBookByBookId(bookId)).thenReturn(Optional.of(book));

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.deleteBookPlace(accountId, bookId));
        verify(coworkingBookRepository, times(1)).findBookByBookId(bookId);
    }

    @Test
    void putBookRoom_WhenUserIsAuthorizedAndRoomIsAvailable_ShouldUpdateBooking() {
        CoworkingSpaceFloorRoomBook roomBook = new CoworkingSpaceFloorRoomBook();

        var account = new Account();
        account.setId(accountId);
        roomBook.setBookingAccount(account);
        roomBook.setCoworkingSpaceFloorRoom(new CoworkingSpaceFloorRoom());

        when(coworkingRoomBookRepository.findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId))
                .thenReturn(Optional.of(roomBook));
        when(coworkingRoomBookRepository.findBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of());
        List<CoworkingSpaceFloorRoomBook> books = new ArrayList<>();
        books.add(roomBook);
        when(coworkingRoomBookRepository.findRoomBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(books);
        when(coworkingRoomBookRepository.save(any())).thenReturn(roomBook);

        var result = coworkingBookService.putBookRoom(accountId, roomId, from, to);

        assertNotNull(result);
        verify(coworkingRoomBookRepository, times(1))
                .findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId);
        verify(coworkingRoomBookRepository, times(1)).save(roomBook);
    }

    @Test
    void putBookRoom_WhenUserIsNotAuthorized_ShouldThrowForbiddenException() {
        var roomBook = new CoworkingSpaceFloorRoomBook();

        var account = new Account();
        account.setId(UUID.randomUUID());
        roomBook.setBookingAccount(account);

        when(coworkingRoomBookRepository.findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId))
                .thenReturn(Optional.of(roomBook));

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.putBookRoom(accountId, roomId, from, to));
        verify(coworkingRoomBookRepository, times(1))
                .findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId);
        verify(coworkingRoomBookRepository, never()).save(any());
    }

    @Test
    void putBookRoom_WhenRoomIsNotAvailable_ShouldThrowConflictException() {
        CoworkingSpaceFloorRoomBook roomBook = new CoworkingSpaceFloorRoomBook();

        var account = new Account();
        account.setId(accountId);
        roomBook.setBookingAccount(account);
        roomBook.setCoworkingSpaceFloorRoom(new CoworkingSpaceFloorRoom());

        when(coworkingRoomBookRepository.findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId))
                .thenReturn(Optional.of(roomBook));
        when(coworkingRoomBookRepository.findBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of(new CoworkingSpaceFloorPlaceBook()));
        when(coworkingRoomBookRepository.findBooksByCoworkingIdAndRoomId(roomId, from, to)).thenReturn(List.of(new CoworkingSpaceFloorPlaceBook()));

        assertThrows(ResponseStatusException.class,
                () -> coworkingBookService.putBookRoom(accountId, roomId, from, to));
        verify(coworkingRoomBookRepository, times(1))
                .findBookByAccountIdAndCoworkingIdAndRoomId(coworkingId, roomId);
        verify(coworkingRoomBookRepository, never()).save(any());
    }

    @Test
    void verifyPlaceBook_WhenAdminAndPlaceBookExists_ShouldReturnResponse() {
        var adminAccount = new Account();
        adminAccount.setIsAdmin(true);
        when(accountService.findById(accountId)).thenReturn(adminAccount);

        var placeBook = new CoworkingSpaceFloorPlaceBook();
        var place = new CoworkingSpaceFloorPlace();
        place.setId(UUID.randomUUID());
        place.setLatitude(55.7558);
        place.setLongitude(37.6176);
        place.setPrice(BigDecimal.valueOf(100.0));
        placeBook.setCoworkingSpaceFloorPlace(place);

        var bookingAccount = new Account();
        bookingAccount.setLogin("admin@example.com");
        placeBook.setBookingAccount(bookingAccount);
        placeBook.setStartTime(LocalDateTime.now());
        placeBook.setEndTime(LocalDateTime.now().plusHours(2));

        var placeBookId = UUID.randomUUID();
        when(coworkingPlaceBookRepository.findById(placeBookId)).thenReturn(Optional.of(placeBook));

        var response = coworkingBookService.verifyPlaceBook(accountId, placeBookId);

        assertNotNull(response);
        assertEquals(place.getLatitude(), response.getLatitude());
        assertEquals(place.getLongitude(), response.getLongitude());
        assertEquals(place.getPrice(), response.getPrice());
        assertEquals(1, response.getBookings().size());
        assertEquals("admin@example.com", response.getBookings().getFirst().getLogin());

        verify(accountService, times(1)).findById(accountId);
        verify(coworkingPlaceBookRepository, times(1)).findById(placeBookId);
    }

    @Test
    void verifyPlaceBook_WhenNotAdmin_ShouldThrowAccessDeniedException() {
        Account nonAdminAccount = new Account();
        nonAdminAccount.setIsAdmin(false);
        when(accountService.findById(accountId)).thenReturn(nonAdminAccount);

        var exception = assertThrows(AccessDeniedException.class,
                () -> coworkingBookService.verifyPlaceBook(accountId, UUID.randomUUID()));
        assertEquals("You don't have permission to get accounts", exception.getMessage());

        verify(accountService, times(1)).findById(accountId);
        verify(coworkingPlaceBookRepository, never()).findById(any());
    }

    @Test
    void verifyPlaceBook_WhenPlaceBookNotFound_ShouldThrowCoworkingNotFoundException() {
        var adminAccount = new Account();
        adminAccount.setIsAdmin(true);
        when(accountService.findById(accountId)).thenReturn(adminAccount);

        var placeBookId = UUID.randomUUID();
        when(coworkingPlaceBookRepository.findById(placeBookId)).thenReturn(Optional.empty());

        var exception = assertThrows(CoworkingNotFoundException.class,
                () -> coworkingBookService.verifyPlaceBook(accountId, placeBookId));
        assertEquals("Coworking place book with id " + placeBookId + " not found", exception.getMessage());

        verify(accountService, times(1)).findById(accountId);
        verify(coworkingPlaceBookRepository, times(1)).findById(placeBookId);
    }
}
