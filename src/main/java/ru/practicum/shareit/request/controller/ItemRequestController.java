package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public List<ItemRequestInfoDto> findAllByUserId(@RequestHeader("X-Sharer-User-Id") final Long userId) {
        log.info(">>> FIND REQUEST BY USER ID: [" + "]");
        return itemRequestService.findAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestInfoDto> findAll(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                            @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL REQUESTS BY USER ID: [" + userId + "]" +
                " >>> WITH PAGINATION FROM: [" + from + "] SIZE: [" + size + "]");

        return itemRequestService.findAllWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestInfoDto findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                       @PathVariable final Long requestId) {
        log.info(">>> FIND REQUEST BY ID: [" + requestId + "]");
        return itemRequestService.findByRequestId(userId, requestId);
    }

    @PostMapping
    public ItemRequestDto save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                               @Valid @RequestBody ItemRequestCreationDto request) {
        log.info(">>> SAVE REQUEST: [" + request + "] >>> BY USER ID: [" + userId + "]");
        return itemRequestService.save(userId, request);
    }
}
