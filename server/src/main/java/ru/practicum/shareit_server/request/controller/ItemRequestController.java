package ru.practicum.shareit_server.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_server.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit_server.request.dto.ItemRequestDto;
import ru.practicum.shareit_server.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit_server.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
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
                                            @RequestParam final Integer from,
                                            @RequestParam final Integer size) {
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
                               @RequestBody ItemRequestCreationDto request) {
        log.info(">>> SAVE REQUEST: [" + request + "] >>> BY USER ID: [" + userId + "]");
        return itemRequestService.save(userId, request);
    }
}
