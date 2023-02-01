package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {

    public static ItemRequest mapToItemRequest(final User user,
                                               final LocalDateTime created,
                                               final ItemRequestCreationDto request) {
        return ItemRequest.builder()
                .requestor(user)
                .created(created)
                .description(request.getDescription())
                .build();
    }

    public static ItemRequestDto mapToItemRequestDto(final ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequestInfoDto mapToItemRequestInfo(final ItemRequest request) {
        return ItemRequestInfoDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }
}
