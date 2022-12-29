package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    private final AtomicLong id = new AtomicLong(1);

    @Override
    public Item findById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAllOwnerItems(final Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(final String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public Item save(final Long ownerId, final Item item) {
        item.setId(id.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(final Long id, final Item item) {
        items.put(id, item);
        return item;
    }
}
