package ru.practicum.shareit_server.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_server.booking.BookingState;
import ru.practicum.shareit_server.booking.dto.BookingCreationDto;
import ru.practicum.shareit_server.booking.dto.BookingDto;
import ru.practicum.shareit_server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
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
                                           @RequestParam final String state,
                                           @RequestParam final Integer from,
                                           @RequestParam final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> USER ID: [" + userId + "]");
        return bookingService.findAllByState(userId, BookingState.valueOf(state), from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByStateForOwner(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                                   @RequestParam final String state,
                                                   @RequestParam final Integer from,
                                                   @RequestParam final Integer size) {
        log.info(">>> FIND ALL BY STATE: [" + state + "] >>> FOR OWNER: [" + userId + "]");
        return bookingService.findAllByStateForOwner(userId, BookingState.valueOf(state), from, size);
    }

    @PostMapping
    public BookingDto save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                           @RequestBody final BookingCreationDto bookingCreationDto) {
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
