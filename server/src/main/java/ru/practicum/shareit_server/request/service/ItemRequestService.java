package ru.practicum.shareit_server.request.service;

import ru.practicum.shareit_server.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit_server.request.dto.ItemRequestDto;
import ru.practicum.shareit_server.request.dto.ItemRequestInfoDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestInfoDto> findAllByUserId(Long userId);

    ItemRequestInfoDto findByRequestId(Long userId, Long requestId);

    List<ItemRequestInfoDto> findAllWithPagination(Long userId, Integer from, Integer size);

    ItemRequestDto save(Long userId, ItemRequestCreationDto request);
}
