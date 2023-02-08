package ru.practicum.shareit_server.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_server.item.dto.CommentCreationDto;
import ru.practicum.shareit_server.item.dto.CommentInfoDto;
import ru.practicum.shareit_server.item.dto.ItemCreationDto;
import ru.practicum.shareit_server.item.dto.ItemInfoDto;
import ru.practicum.shareit_server.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemInfoDto findById(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                @PathVariable final Long id) {
        log.info(">>> FIND ITEM BY ID: [" + id + "] >> USER ID: [" + userId + "]");
        return itemService.findById(userId, id);
    }

    @GetMapping
    public List<ItemInfoDto> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        log.info(">>> FIND ALL ITEMS BY USER ID: [" + ownerId + "]");
        return itemService.findAllOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemCreationDto> search(@RequestParam final String text,
                                        @RequestParam Integer from,
                                        @RequestParam Integer size) {
        log.info(">>> SEARCH ITEM BY TEXT: [" + text + "]");
        return itemService.search(text, from, size);
    }

    @PostMapping
    public ItemCreationDto save(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                @RequestBody final ItemCreationDto itemCreationDto) {
        log.info(">>> SAVE ITEM: [" + itemCreationDto + "] >>> BY USER ID: [" + userId + "]");
        return itemService.save(userId, itemCreationDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfoDto addComment(@RequestHeader("X-Sharer-User-Id") final Long userId,
                                     @PathVariable final Long itemId,
                                     @RequestBody final CommentCreationDto commentCreationDto) {
        log.info(">>> ADD COMMENT: [" + commentCreationDto + "]" +
                " >>> TO ITEM ID: [" + itemId + "] >>> BY USER ID: [" + userId + "]");

        return itemService.addComment(userId, itemId, commentCreationDto);
    }

    @PatchMapping("/{id}")
    public ItemCreationDto update(@PathVariable final Long id,
                                  @RequestHeader("X-Sharer-User-Id") final Long ownerId,
                                  @RequestBody final ItemCreationDto itemCreationDto) {
        log.info(">>> UPDATE ITEM: [" + itemCreationDto + "] >>> ID: [" + id + "]" +
                " >>> BY USER ID: [" + ownerId + "]");
        return itemService.update(id, ownerId, itemCreationDto);
    }
}
