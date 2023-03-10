package ru.practicum.shareit_server.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit_server.booking.mapper.BookingMapper;
import ru.practicum.shareit_server.booking.model.Booking;
import ru.practicum.shareit_server.booking.model.BookingStatus;
import ru.practicum.shareit_server.booking.repository.BookingRepository;
import ru.practicum.shareit_server.exception.AccessDeniedException;
import ru.practicum.shareit_server.exception.NotFoundException;
import ru.practicum.shareit_server.exception.UnavailableException;
import ru.practicum.shareit_server.item.dto.CommentCreationDto;
import ru.practicum.shareit_server.item.dto.CommentInfoDto;
import ru.practicum.shareit_server.item.dto.ItemCreationDto;
import ru.practicum.shareit_server.item.dto.ItemInfoDto;
import ru.practicum.shareit_server.item.mapper.CommentMapper;
import ru.practicum.shareit_server.item.mapper.ItemMapper;
import ru.practicum.shareit_server.item.model.Comment;
import ru.practicum.shareit_server.item.model.Item;
import ru.practicum.shareit_server.item.repository.CommentRepository;
import ru.practicum.shareit_server.item.repository.ItemRepository;
import ru.practicum.shareit_server.request.model.ItemRequest;
import ru.practicum.shareit_server.request.repository.ItemRequestRepository;
import ru.practicum.shareit_server.user.mapper.UserMapper;
import ru.practicum.shareit_server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemInfoDto findById(final Long userId, final Long id) {
        final var currentTime = LocalDateTime.now();
        userService.findById(userId);
        final var itemInfoDto = ItemMapper.mapToItemBookingDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")));
        final var comments = CommentMapper.mapToCommentInfoDto(commentRepository.findAllByItemId(id));
        itemInfoDto.setComments(comments);

        if (!itemInfoDto.getOwnerId().equals(userId)) {
            return itemInfoDto;
        }

        final var bookings = bookingRepository.findAllByItemId(id, Sort.by("start").descending());

        return setBooking(currentTime, itemInfoDto, bookings);
    }

    @Override
    public List<ItemInfoDto> findAllOwnerItems(final Long ownerId, final Integer from, final Integer size) {
        final var currentTime = LocalDateTime.now();
        userService.findById(ownerId);

        final var pageable = PageRequest.of(from / size, size, Sort.unsorted());
        final var itemsInfoDto = itemRepository.findByOwnerId(ownerId, pageable).stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::mapToItemBookingDto)
                .collect(Collectors.toList());

        final var itemIds = itemsInfoDto.stream()
                .map(ItemInfoDto::getId)
                .collect(Collectors.toList());

        final var bookings = bookingRepository.findAllByItemIdIn(itemIds,
                Sort.by("start").descending());
        final var comments = commentRepository.findAllByItemIdIn(itemIds);

        return setCommentAndBooking(currentTime, bookings, itemsInfoDto, comments);
    }

    @Override
    public List<ItemCreationDto> search(final String text, final Integer from, final Integer size) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        final var validText = text.toLowerCase().trim();
        final var pageable = PageRequest.of(from / size, size, Sort.unsorted());

        return itemRepository.search(validText, pageable)
                .stream()
                .map(ItemMapper::mapToItemCreationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemCreationDto save(final Long ownerId, final ItemCreationDto itemCreationDto) {
        userService.findById(ownerId);
        final var requestId = itemCreationDto.getRequestId();
        final ItemRequest request;

        if (requestId != null) {
            request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        } else {
            request = null;
        }

        final var item = ItemMapper.mapToItem(itemCreationDto, ownerId, request);

        return ItemMapper.mapToItemCreationDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentInfoDto addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) {
        final var created = LocalDateTime.now();
        final var user = UserMapper.mapToUser(userService.findById(userId));
        final var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        bookingRepository.findFirstByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId,
                        BookingStatus.APPROVED,
                        created)
                .orElseThrow(() -> new UnavailableException("You cannot comment on this item"));

        commentCreationDto.setCreated(created);
        final var comment = CommentMapper.mapToComment(user, item, commentCreationDto);

        return CommentMapper.mapToCommentInfoDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public ItemCreationDto update(final Long id, final Long ownerId, final ItemCreationDto itemCreationDto) {
        userService.findById(ownerId);

        final var item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Access is denied");
        }

        if (itemCreationDto.getName() != null && !itemCreationDto.getName().isEmpty()) {
            item.setName(itemCreationDto.getName());
        }

        if (itemCreationDto.getDescription() != null && !itemCreationDto.getDescription().isEmpty()) {
            item.setDescription(itemCreationDto.getDescription());
        }

        if (itemCreationDto.getAvailable() != null) {
            item.setAvailable(itemCreationDto.getAvailable());
        }

        return ItemMapper.mapToItemCreationDto(itemRepository.save(item));
    }

    private ItemInfoDto setBooking(final LocalDateTime currentTime,
                                   final ItemInfoDto itemInfoDto,
                                   final List<Booking> bookings) {
        final var lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(currentTime))
                .findFirst()
                .map(BookingMapper::mapToBookingShortDto)
                .orElse(null);
        final var nextBooking = bookings.stream()
                .filter(booking -> booking.getEnd().isAfter(currentTime))
                .map(BookingMapper::mapToBookingShortDto)
                .findFirst()
                .orElse(null);

        itemInfoDto.setLastBooking(lastBooking);
        itemInfoDto.setNextBooking(nextBooking);

        return itemInfoDto;
    }

    private List<ItemInfoDto> setCommentAndBooking(final LocalDateTime currentTime,
                                                   final List<Booking> bookings,
                                                   final List<ItemInfoDto> itemsInfoDto,
                                                   final List<Comment> comments) {
        for (ItemInfoDto item : itemsInfoDto) {
            final var lastBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .filter(booking -> booking.getStart().isBefore(currentTime))
                    .map(BookingMapper::mapToBookingShortDto)
                    .findFirst()
                    .orElse(null);
            final var nextBooking = bookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(item.getId()))
                    .filter(booking -> booking.getEnd().isAfter(currentTime))
                    .map(BookingMapper::mapToBookingShortDto)
                    .findFirst()
                    .orElse(null);
            final var itemComments = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .map(CommentMapper::mapToCommentInfoDto)
                    .collect(Collectors.toList());

            item.setComments(itemComments);
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }

        return itemsInfoDto;
    }
}
