package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemMapper {

    public static ItemCreationDto mapToItemCreationDto(final Item item) {
        final var requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return ItemCreationDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();
    }

    public static ItemInfoDto mapToItemBookingDto(final Item item) {
        return ItemInfoDto.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    public static ItemShortDto mapToItemShortDto(final Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static Item mapToItem(final ItemCreationDto itemDto, final Long ownerId, final ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .ownerId(ownerId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(request)
                .build();
    }
}
