package ru.practicum.shareit_gateway.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gateway.request.client.ItemRequestClient;
import ru.practicum.shareit_gateway.request.dto.ItemRequestCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader("X-Sharer-User-Id") final Long userId) {
        log.info(">>> FIND REQUEST BY USER ID: [" + "]");
        return itemRequestClient.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAll(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                          @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL REQUESTS BY USER ID: [" + userId + "]" +
                " >>> WITH PAGINATION FROM: [" + from + "] SIZE: [" + size + "]");

        return itemRequestClient.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                           @PathVariable final Long requestId) {
        log.info(">>> FIND REQUEST BY ID: [" + requestId + "]");
        return itemRequestClient.findByRequestId(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                       @Valid @RequestBody ItemRequestCreationDto request) {
        log.info(">>> SAVE REQUEST: [" + request + "] >>> BY USER ID: [" + userId + "]");
        return itemRequestClient.save(userId, request);
    }
}
