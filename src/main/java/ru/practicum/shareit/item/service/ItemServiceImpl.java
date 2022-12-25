package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto findById(final Long id) {
        return ItemMapper.mapToItemDto(itemStorage.findById(id));
    }

    @Override
    public List<ItemDto> findAllOwnerItems(final Long ownerId) {
        if (userStorage.findById(ownerId) == null) {
            throw new NotFoundException("User does not exist");
        }

        return itemStorage.findAllOwnerItems(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        final var validText = text.toLowerCase().trim();

        return itemStorage.search(validText).stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto save(final Long ownerId, final ItemDto itemDto) {
        if (userStorage.findById(ownerId) == null) {
            throw new NotFoundException("User does not exist");
        }

        final var item = ItemMapper.mapToItem(itemDto, ownerId);

        return ItemMapper.mapToItemDto(itemStorage.save(ownerId, item));
    }

    @Override
    public ItemDto update(final Long id, final Long ownerId, final ItemDto itemDto) {
        final var item = itemStorage.findById(id);

        if (userStorage.findById(ownerId) == null) {
            throw new NotFoundException("User does not exist");
        }

        if (item == null) {
            throw new NotFoundException("Item does not exist");
        }

        if (!item.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access is denied");
        }

        if (itemDto.getName() != null && !itemDto.getName().isEmpty()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isEmpty()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.mapToItemDto(itemStorage.update(id, item));
    }
}
