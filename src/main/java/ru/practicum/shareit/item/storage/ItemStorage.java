package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item findById(Long id);

    List<Item> findAllOwnerItems(Long ownerId);

    List<Item> search(String text);

    Item save(Long ownerId, Item item);

    Item update(Long id, Item item);
}
