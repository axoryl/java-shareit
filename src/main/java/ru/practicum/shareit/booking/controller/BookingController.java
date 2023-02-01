package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                               @PathVariable final Long bookingId) {
        log.info(">>> FIND BY ID: [" + bookingId + "]");
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> findAllByState(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                           @RequestParam(required = false, defaultValue = "ALL") final String state,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                           @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> USER ID: [" + userId + "]");

        try {
            return bookingService.findAllByState(userId, BookingState.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByStateForOwner(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                   @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                                   @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> FOR OWNER: [" + userId + "]");

        try {
            return bookingService.findAllByStateForOwner(userId, BookingState.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @PostMapping
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                           @Valid @RequestBody final BookingCreationDto bookingCreationDto) {
        log.info(">>> SAVE BOOKING: [" + bookingCreationDto + "]");
        return bookingService.save(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                              @PathVariable final Long bookingId,
                              @RequestParam(name = "approved") final Boolean isApprove) {
        log.info(">>> APPROVED BY USER ID: [" + ownerId + "] >> BOOKING ID: [" + bookingId + "] " +
                " >>> APPROVED STATUS: [" + isApprove + "]");

        return bookingService.approve(ownerId, bookingId, isApprove);
    }
}
