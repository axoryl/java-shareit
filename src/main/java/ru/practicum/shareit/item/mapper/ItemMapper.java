package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemCreationDto mapToItemCreationDto(final Item item) {
        return ItemCreationDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
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

    public static Item mapToItem(final ItemCreationDto itemCreationDto, final Long ownerId) {
        return Item.builder()
                .id(itemCreationDto.getId())
                .ownerId(ownerId)
                .name(itemCreationDto.getName())
                .description(itemCreationDto.getDescription())
                .available(itemCreationDto.getAvailable())
                .build();
    }
}
