package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

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

    @Override
    public ItemInfoDto findById(final Long userId, final Long id) {
        final var itemInfoDto = ItemMapper.mapToItemBookingDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")));
        final List<CommentInfoDto> comments = CommentMapper.mapToCommentInfoDto(commentRepository.findAllByItemId(id));
        itemInfoDto.setComments(comments);

        if (!itemInfoDto.getOwnerId().equals(userId)) {
            return itemInfoDto;
        }

        final var currentTime = LocalDateTime.now();
        final List<Booking> bookings = bookingRepository.findAllByItemId(id,
                Sort.by("start").descending());

        return setBooking(currentTime, itemInfoDto, bookings);
    }

    @Override
    public List<ItemInfoDto> findAllOwnerItems(final Long ownerId) {
        userService.findById(ownerId);

        final List<ItemInfoDto> itemsInfoDto = itemRepository.findByOwnerId(ownerId).stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::mapToItemBookingDto)
                .collect(Collectors.toList());

        final var itemIds = itemsInfoDto.stream()
                .map(ItemInfoDto::getId)
                .collect(Collectors.toList());

        final List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemIds,
                Sort.by("start").descending());
        final List<Comment> comments = commentRepository.findAllByItemIdIn(itemIds);
        final var currentTime = LocalDateTime.now();

        return setCommentAndBooking(currentTime, bookings, itemsInfoDto, comments);
    }

    @Override
    public List<ItemCreationDto> search(final String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        final var validText = text.toLowerCase().trim();

        return itemRepository.search(validText)
                .stream()
                .map(ItemMapper::mapToItemCreationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemCreationDto save(final Long ownerId, final ItemCreationDto itemCreationDto) {
        userService.findById(ownerId);
        final var item = ItemMapper.mapToItem(itemCreationDto, ownerId);

        return ItemMapper.mapToItemCreationDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public CommentInfoDto addComment(Long userId, Long itemId, CommentCreationDto commentCreationDto) {
        final var user = UserMapper.mapToUser(userService.findById(userId));
        final var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        final var created = LocalDateTime.now();

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
                .filter(o -> o.getStart().isBefore(currentTime))
                .findFirst()
                .map(BookingMapper::mapToBookingShortDto)
                .orElse(null);
        final var nextBooking = bookings.stream()
                .filter(o -> o.getEnd().isAfter(currentTime))
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
                                                   final List<Comment> comments){
        for (ItemInfoDto item : itemsInfoDto) {
            final var lastBooking = bookings.stream()
                    .filter(o -> o.getItem().getId().equals(item.getId()))
                    .filter(o -> o.getStart().isBefore(currentTime))
                    .map(BookingMapper::mapToBookingShortDto)
                    .findFirst()
                    .orElse(null);
            final var nextBooking = bookings.stream()
                    .filter(o -> o.getItem().getId().equals(item.getId()))
                    .filter(o -> o.getEnd().isAfter(currentTime))
                    .map(BookingMapper::mapToBookingShortDto)
                    .findFirst()
                    .orElse(null);
            final var itemComments = comments.stream()
                    .filter(o -> o.getItem().getId().equals(item.getId()))
                    .map(CommentMapper::mapToCommentInfoDto)
                    .collect(Collectors.toList());

            item.setComments(itemComments);
            item.setLastBooking(lastBooking);
            item.setNextBooking(nextBooking);
        }

        return itemsInfoDto;
    }
}
