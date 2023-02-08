package ru.practicum.shareit_gateway.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gateway.booking.BookingState;
import ru.practicum.shareit_gateway.booking.client.BookingClient;
import ru.practicum.shareit_gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit_gateway.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                           @PathVariable final Long bookingId) {
        log.info(">>> FIND BY ID: [" + bookingId + "]");
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByState(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                 @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                                 @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> USER ID: [" + userId + "]");

        try {
            return bookingClient.findAllByState(userId, BookingState.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByStateForOwner(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL") final String state,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                                         @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> FOR OWNER: [" + userId + "]");

        try {
            return bookingClient.findAllByStateForOwner(userId, BookingState.valueOf(state), from, size);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStateException("Unknown state: " + state);
        }
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                       @Valid @RequestBody final BookingCreationDto bookingCreationDto) {
        log.info(">>> SAVE BOOKING: [" + bookingCreationDto + "]");
        return bookingClient.save(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                          @PathVariable final Long bookingId,
                                          @RequestParam(name = "approved") final Boolean isApprove) {
        log.info(">>> APPROVED BY USER ID: [" + ownerId + "] >> BOOKING ID: [" + bookingId + "] " +
                " >>> APPROVED STATUS: [" + isApprove + "]");

        return bookingClient.approve(ownerId, bookingId, isApprove);
    }
}
