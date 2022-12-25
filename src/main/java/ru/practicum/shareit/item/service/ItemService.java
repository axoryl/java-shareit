package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto findById(Long id);

    List<ItemDto> findAllOwnerItems(Long ownerId);

    List<ItemDto> search(String text);

    ItemDto save(Long ownerId, ItemDto itemDto);

    ItemDto update(Long id, Long ownerId, ItemDto itemDto);
}
