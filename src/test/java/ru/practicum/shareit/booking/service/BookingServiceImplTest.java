package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDateTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusAlreadySetException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void findBookingById_whenInvoked_thenReturnedBooking() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var bookingForOwner = bookingService.findById(ownerId, bookingId);
        final var bookingForBooker = bookingService.findById(booker.getId(), bookingId);

        assertAll(
                () -> assertEquals(bookingId, bookingForOwner.getId()),
                () -> assertEquals(booking.getStart(), bookingForOwner.getStart()),
                () -> assertEquals(booking.getEnd(), bookingForOwner.getEnd()),
                () -> assertEquals(booking.getStatus(), bookingForOwner.getStatus())
        );

        assertAll(
                () -> assertEquals(bookingId, bookingForBooker.getId()),
                () -> assertEquals(booking.getStart(), bookingForBooker.getStart()),
                () -> assertEquals(booking.getEnd(), bookingForBooker.getEnd()),
                () -> assertEquals(booking.getStatus(), bookingForBooker.getStatus())
        );
    }

    @Test
    void findBookingById_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(99L, 1L));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void findBookingById_whenBookingNotFound_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(ownerId, 99L));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void findBookingById_whenItemNotFound_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        item.setId(99L);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(ownerId, bookingId));

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void findBookingById_whenInvokedByNotOwnerOrBooker_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        final var anotherUser = getUser();
        anotherUser.setEmail("eeee@to.tos");
        final var anotherUserId = userRepository.save(anotherUser).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findById(anotherUserId, bookingId));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithDefaultState_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state all
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.ALL, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithStateCurrent_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state current
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.CURRENT, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithStatePast_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state past
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(3));
        booking.setEnd(LocalDateTime.now().minusHours(2));
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.PAST, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithStateFuture_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state future
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.FUTURE, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithStateWaiting_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state waiting
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.WAITING, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenInvokedWithStateRejected_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state rejected
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.REJECTED, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByStateForOwner_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllByStateForOwner(99L, BookingState.ALL, 0, 10));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void findAllBookingsByStateForOwner_whenItemsNotFound_thenReturnedEmptyList() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        userRepository.save(booker);

        final var bookings = bookingService.findAllByStateForOwner(ownerId, BookingState.ALL, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(0, bookings.size())
        );
    }

    @Test
    void findAllBookingsByState_whenInvokedWithDefaultStateAll_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state all
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.ALL, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllBookingsByState_whenInvokedWithStateCurrent_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state current
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.CURRENT, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllByState_whenInvokedWithStatePast_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state past
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusHours(3));
        booking.setEnd(LocalDateTime.now().minusHours(2));
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.PAST, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllByState_whenInvokedWithStateFuture_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state future
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusHours(2));
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.FUTURE, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllByState_whenInvokedWithStateWaiting_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state waiting
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.WAITING, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllByState_whenInvokedWithStateRejected_thenReturnedBookings() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var userId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking - state rejected
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);

        final var bookings = bookingService.findAllByState(userId, BookingState.REJECTED, 0, 10);

        assertAll(
                () -> assertNotNull(bookings),
                () -> assertEquals(1, bookings.size())
        );
    }

    @Test
    void findAllByState_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.findAllByState(99L, BookingState.ALL, 0, 10));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void save_whenInvoked_thenReturnedBooking() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var bookerId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBookingCreationDto();
        booking.setItemId(item.getId());

        final var actualBooking = bookingService.save(bookerId, booking);

        assertAll(
                () -> assertNotNull(actualBooking),
                () -> assertEquals(booking.getStart(), actualBooking.getStart()),
                () -> assertEquals(booking.getEnd(), actualBooking.getEnd())
        );
    }

    @Test
    void save_whenUserNotFound_thenNotFoundException() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBookingCreationDto();
        booking.setItemId(item.getId());

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(99L, booking));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void save_whenItemNotFound_thenNotFoundException() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create booking
        final var booking = getBookingCreationDto();
        booking.setItemId(99L);

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(userId, booking));

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void save_whenItemUnavailable_thenUnavailableExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var bookerId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setAvailable(false);
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBookingCreationDto();
        booking.setItemId(item.getId());

        final var exception = assertThrows(UnavailableException.class,
                () -> bookingService.save(bookerId, booking));

        assertEquals("Item unavailable", exception.getMessage());
    }

    @Test
    void save_whenInvalidDates_thenIncorrectDateTimeExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var bookerId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBookingCreationDto();
        booking.setStart(LocalDateTime.now().plusMinutes(1));
        booking.setEnd(LocalDateTime.now());
        booking.setItemId(item.getId());

        final var exception = assertThrows(IncorrectDateTimeException.class,
                () -> bookingService.save(bookerId, booking));

        assertEquals("Invalid booking date: " +
                        "start[" + booking.getStart() + "] <<>> end[" + booking.getEnd() + "]",
                exception.getMessage());
    }

    @Test
    void save_whenOwnerBookItem_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBookingCreationDto();
        booking.setItemId(item.getId());

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.save(ownerId, booking));

        assertEquals("The owner cannot book his item", exception.getMessage());
    }

    @Test
    void approve_whenInvoked_thenApproveBooking() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("new@t.to");
        final var booker = userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var actualBooking = bookingService.approve(ownerId, bookingId, true);

        assertAll(
                () -> assertNotNull(actualBooking),
                () -> assertEquals(BookingStatus.APPROVED, actualBooking.getStatus())
        );
    }

    @Test
    void approve_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(99L, 1L, true));

        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    void approve_whenBookingNotFound_thenNotFoundExceptionThrown() {
        final var ownerId = userRepository.save(getUser()).getId();
        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(ownerId, 99L, true));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void approve_whenInvokedNotOwner_thenNotFoundException() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var booker = getUser();
        booker.setEmail("new@t.to");
        final var bookerId = userRepository.save(booker).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(bookerId, bookingId, true));

        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void approve_whenInvokedAndStatusAlreadySet_thenStatusAlreadySetException() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("new@t.to");
        final var booker = userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = getBooking();
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setItem(item);
        final var bookingId = bookingRepository.save(booking).getId();

        final var exception = assertThrows(StatusAlreadySetException.class,
                () -> bookingService.approve(ownerId, bookingId, true));

        assertEquals("Status of booking is already " + booking.getStatus(), exception.getMessage());
    }

    private User getUser() {
        return User.builder()
                .email("email@t.to")
                .name("user")
                .build();
    }

    private BookingCreationDto getBookingCreationDto() {
        return BookingCreationDto.builder()
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();
    }

    private Item getItem() {
        return Item.builder()
                .description("desc")
                .available(true)
                .name("item")
                .build();
    }

    private Booking getBooking() {
        return Booking.builder()
                .start(LocalDateTime.now().minusMinutes(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.WAITING)
                .build();
    }
}
