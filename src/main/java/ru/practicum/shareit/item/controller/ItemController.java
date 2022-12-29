package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemDto findById(@PathVariable Long id) {
        log.info("find item by id " + id);
        return itemService.findById(id);
    }

    @GetMapping
    public List<ItemDto> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("find all items by user id " + ownerId);
        return itemService.findAllOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("search item");
        return itemService.search(text);
    }

    @PostMapping
    public ItemDto save(@RequestHeader("X-Sharer-User-Id") long ownerId,
                        @Valid @RequestBody ItemDto itemDto) {
        log.info("save item");
        return itemService.save(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable Long id,
                          @RequestHeader("X-Sharer-User-Id") long ownerId,
                          @RequestBody ItemDto itemDto) {
        log.info("update item, id " + id);
        return itemService.update(id, ownerId, itemDto);
    }
}
