package ru.practicum.shareit_server.item.service;

import ru.practicum.shareit_server.item.dto.CommentCreationDto;
import ru.practicum.shareit_server.item.dto.CommentInfoDto;
import ru.practicum.shareit_server.item.dto.ItemCreationDto;
import ru.practicum.shareit_server.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {

    ItemInfoDto findById(Long ownerId, Long id);

    List<ItemInfoDto> findAllOwnerItems(Long ownerId, Integer from, Integer size);

    List<ItemCreationDto> search(String text, Integer from, Integer size);

    ItemCreationDto save(Long ownerId, ItemCreationDto itemCreationDto);

    CommentInfoDto addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto);

    ItemCreationDto update(Long id, Long ownerId, ItemCreationDto itemCreationDto);
}
