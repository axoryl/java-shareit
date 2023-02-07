package ru.practicum.shareit_gateway.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gateway.exception.ValidationException;
import ru.practicum.shareit_gateway.item.client.ItemClient;
import ru.practicum.shareit_gateway.item.dto.CommentCreationDto;
import ru.practicum.shareit_gateway.item.dto.ItemCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                           @PathVariable final Long itemId) {
        log.info(">>> FIND ITEM BY ID: [" + itemId + "] >> USER ID: [" + userId + "]");
        return itemClient.findById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                                    @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> FIND ALL ITEMS BY USER ID: [" + ownerId + "]");
        return itemClient.findAllOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam final String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero final Integer from,
                                         @RequestParam(defaultValue = "20") @Positive final Integer size) {
        log.info(">>> SEARCH ITEM BY TEXT: [" + text + "]");

        return itemClient.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                       @Valid @RequestBody final ItemCreationDto itemCreationDto) {
        log.info(">>> SAVE ITEM: [" + itemCreationDto + "] >>> BY USER ID: [" + userId + "]");
        return itemClient.save(userId, itemCreationDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                             @PathVariable final Long itemId,
                                             @RequestBody final CommentCreationDto commentCreationDto) {
        log.info(">>> ADD COMMENT: [" + commentCreationDto + "]" +
                " >>> TO ITEM ID: [" + itemId + "] >>> BY USER ID: [" + userId + "]");

        if (commentCreationDto.getText().isBlank()) {
            throw new ValidationException("Comment is blank");
        }

        return itemClient.addComment(userId, itemId, commentCreationDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable final Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                         @RequestBody final ItemCreationDto itemCreationDto) {
        log.info(">>> UPDATE ITEM: [" + itemCreationDto + "] >>> ID: [" + itemId + "]" +
                " >>> BY USER ID: [" + ownerId + "]");
        return itemClient.update(itemId, ownerId, itemCreationDto);
    }
}
