package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByState(Long userId, BookingState state);

    List<BookingDto> findAllByStateForOwner(Long userId, BookingState state);

    BookingDto save(Long userId, BookingCreationDto bookingCreationDto);

    BookingDto approve(Long ownerId, Long bookingId, Boolean approved);
}
