package ru.practicum.shareit_server.booking.service;

import ru.practicum.shareit_server.booking.BookingState;
import ru.practicum.shareit_server.booking.dto.BookingCreationDto;
import ru.practicum.shareit_server.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByState(Long userId, BookingState state, Integer from, Integer size);

    List<BookingDto> findAllByStateForOwner(Long userId, BookingState state, Integer from, Integer size);

    BookingDto save(Long userId, BookingCreationDto bookingCreationDto);

    BookingDto approve(Long ownerId, Long bookingId, Boolean approved);
}
