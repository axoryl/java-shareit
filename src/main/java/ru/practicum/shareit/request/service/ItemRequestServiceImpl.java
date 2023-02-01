package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestInfoDto> findAllByUserId(final Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        final var requests = itemRequestRepository.findAllByRequestorId(userId,
                        Sort.by("created").descending()).stream()
                .map(ItemRequestMapper::mapToItemRequestInfo)
                .collect(Collectors.toList());
        final var requestIds = requests.stream()
                .map(ItemRequestInfoDto::getId)
                .collect(Collectors.toList());
        final var items = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::mapToItemCreationDto)
                .collect(Collectors.toList());

        return setItems(requests, items);
    }

    @Override
    public ItemRequestInfoDto findByRequestId(final Long userId, final Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        final var request = mapToItemRequestInfo(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found")));
        final var items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::mapToItemCreationDto)
                .collect(Collectors.toList());

        request.setItems(items);

        return request;
    }

    @Override
    public List<ItemRequestInfoDto> findAllWithPagination(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        final var pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        final var requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable).stream()
                .map(ItemRequestMapper::mapToItemRequestInfo)
                .collect(Collectors.toList());
        final var requestIds = requests.stream()
                .map(ItemRequestInfoDto::getId)
                .collect(Collectors.toList());
        final var items = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::mapToItemCreationDto)
                .collect(Collectors.toList());

        return setItems(requests, items);
    }

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestCreationDto request) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        final var currentTime = LocalDateTime.now();
        final var itemRequest = mapToItemRequest(user, currentTime, request);

        return mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    private List<ItemRequestInfoDto> setItems(final List<ItemRequestInfoDto> requests,
                                              final List<ItemCreationDto> items) {
        for (ItemRequestInfoDto request : requests) {
            final var requestItems = items.stream()
                    .filter(item -> item.getRequestId().equals(request.getId()))
                    .collect(Collectors.toList());

            request.setItems(requestItems);
        }

        return requests;
    }
}
