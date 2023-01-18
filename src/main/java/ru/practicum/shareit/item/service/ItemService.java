package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {

    ItemInfoDto findById(Long ownerId, Long id);

    List<ItemInfoDto> findAllOwnerItems(Long ownerId);

    List<ItemCreationDto> search(String text);

    ItemCreationDto save(Long ownerId, ItemCreationDto itemCreationDto);

    CommentInfoDto addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto);

    ItemCreationDto update(Long id, Long ownerId, ItemCreationDto itemCreationDto);
}
