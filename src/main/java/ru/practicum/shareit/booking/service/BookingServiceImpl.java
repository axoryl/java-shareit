package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectDateTimeException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StatusAlreadySetException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto findById(final Long userId, final Long bookingId) {
        final var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        userService.findById(userId);
        itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (booking.getBooker().getId().equals(userId)
                || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.mapToBookingDto(booking);
        }
        throw new NotFoundException("Booking not found");
    }

    @Override
    public List<BookingDto> findAllByStateForOwner(final Long userId, final BookingState state) {
        userService.findById(userId);

        final List<Long> itemsId = itemRepository.findByOwnerId(userId).stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        if (itemsId.isEmpty()) {
            return new ArrayList<>();
        }

        final var currentTime = LocalDateTime.now();
        final List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findByCurrentTime(currentTime, itemsId));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByEndBeforeAndItemIdIn(currentTime, itemsId,
                        Sort.by("start").descending()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByStartAfterAndItemIdIn(currentTime, itemsId,
                        Sort.by("start").descending()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByStatusAndItemIdIn(BookingStatus.WAITING, itemsId,
                        Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByStatusAndItemIdIn(BookingStatus.REJECTED, itemsId,
                        Sort.by("start").descending()));
                break;
            default:
                bookings.addAll(bookingRepository.findAllByItemIdIn(itemsId,
                        Sort.by("start").descending()));
                break;
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByState(final Long userId, final BookingState state) {
        userService.findById(userId);

        final var currentTime = LocalDateTime.now();
        final List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findByCurrentTime(userId, currentTime));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByBookerIdAndEndBefore(userId, currentTime,
                        Sort.by("start").descending()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByBookerIdAndStartAfter(userId, currentTime,
                        Sort.by("start").descending()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING,
                        Sort.by("start").descending()));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED,
                        Sort.by("start").descending()));
                break;
            default:
                bookings.addAll(bookingRepository.findAllByBookerId(userId,
                        Sort.by("start").descending()));
                break;
        }

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto save(final Long userId, final BookingCreationDto bookingCreationDto) {
        final var user = UserMapper.mapToUser(userService.findById(userId));
        final var item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new UnavailableException("Item unavailable");
        }

        final var startDate = bookingCreationDto.getStart();
        final var endDate = bookingCreationDto.getEnd();

        if (startDate.compareTo(endDate) >= 0) {
            throw new IncorrectDateTimeException("Invalid booking date: " +
                    "start[" + startDate + "] <<>> end[" + endDate + "]");
        }

        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("The owner cannot book his item");
        }

        final var booking = BookingMapper.mapToBooking(bookingCreationDto, user, item);
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(final Long ownerId, final Long bookingId, final Boolean isApprove) {
        userService.findById(ownerId);
        final var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        final var item = booking.getItem();

        if (!ownerId.equals(item.getOwnerId())) {
            throw new NotFoundException("Booking not found");
        }

        if (isApprove && booking.getStatus().equals(BookingStatus.APPROVED)
                || !isApprove && booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new StatusAlreadySetException("Status of booking is already " + booking.getStatus());
        }
        booking.setStatus(isApprove ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }
}
