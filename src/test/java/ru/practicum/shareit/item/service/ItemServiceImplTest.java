package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    @Test
    void findItemById_whenInvoked_thenReturnedItem() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("newemail@t.to");
        userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create bookings
        final var lastBooking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        final var nextBooking = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

        final var actualItem = itemService.findById(user.getId(), item.getId());

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "item")
                .hasFieldOrPropertyWithValue("description", "desc")
                .hasFieldOrPropertyWithValue("ownerId", ownerId);

        assertThat(actualItem.getComments())
                .isNotNull()
                .isEmpty();

        assertNull(actualItem.getLastBooking());
        assertNull(actualItem.getNextBooking());
    }

    @Test
    void findItemById_whenItemNotFound_thenNotFoundExceptionThrown() {
        final var userId = userRepository.save(getUser()).getId();

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(userId, 99L));

        assertThat(exception.getMessage())
                .isEqualTo("Item not found");
    }

    @Test
    void findItemById_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var userId = userRepository.save(getUser()).getId();
        final var item = getItem();
        item.setOwnerId(userId);
        itemRepository.save(item);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(99L, item.getId()));

        assertThat(exception.getMessage())
                .isEqualTo("User does not exist");
    }

    @Test
    void findItemById_whenUserIsOwner_thenReturnedItem() {
        // create users
        final var owner = userRepository.save(getUser());
        final var booker = getUser();
        booker.setEmail("newemail@w.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(owner.getId());
        itemRepository.save(item);
        // create bookings
        final var lastBooking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        final var nextBooking = Booking.builder()
                .booker(booker)
                .item(item)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        bookingRepository.save(lastBooking);
        bookingRepository.save(nextBooking);

        final var actualItem = itemService.findById(owner.getId(), item.getId());

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "item")
                .hasFieldOrPropertyWithValue("description", "desc")
                .hasFieldOrPropertyWithValue("ownerId", owner.getId());

        assertThat(actualItem.getComments())
                .isNotNull()
                .isEmpty();

        assertThat(actualItem.getLastBooking())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", lastBooking.getId());
        assertThat(actualItem.getNextBooking())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", nextBooking.getId());
    }

    @Test
    void findItemById_whenItemWithComments_thenReturnedItem() {
        // create users
        final var owner = userRepository.save(getUser());
        final var booker = getUser();
        booker.setEmail("newemail@t.to");
        userRepository.save(booker);
        // create item
        final var item = getItem();
        item.setOwnerId(owner.getId());
        itemRepository.save(item);
        // create booking
        final var booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        bookingRepository.save(booking);
        // create comments
        commentRepository.save(
                Comment.builder()
                        .created(LocalDateTime.now())
                        .item(item)
                        .author(booker)
                        .text("Wow, i'm commenting.")
                        .build()
        );
        commentRepository.save(
                Comment.builder()
                        .created(LocalDateTime.now())
                        .item(item)
                        .author(booker)
                        .text("And one more.")
                        .build()
        );

        final var actualItem = itemService.findById(owner.getId(), item.getId());

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "item")
                .hasFieldOrPropertyWithValue("description", "desc")
                .hasFieldOrPropertyWithValue("ownerId", owner.getId());

        assertThat(actualItem.getComments())
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void findAllOwnerItems_whenInvoked_thenReturnedItems() {
        final var ownerId = userRepository.save(getUser()).getId();
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);

        final var actualItems = itemService.findAllOwnerItems(ownerId, 0, 10);

        assertThat(actualItems)
                .isNotNull()
                .hasSize(1);

        assertThat(actualItems.get(0))
                .hasFieldOrPropertyWithValue("id", item.getId())
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription());
    }

    @Test
    void findAllOwnerItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findAllOwnerItems(99L, 0, 10));

        assertThat(exception.getMessage())
                .isEqualTo("User does not exist");
    }

    @Test
    void search_whenInvoked_thenReturnedItems() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create items
        final var item = getItem();
        item.setOwnerId(userId);
        item.setDescription("description with keyword -> {found}");
        final var item2 = getItem();
        item2.setOwnerId(userId);
        item2.setName("keyword -> {found}");
        itemRepository.save(item);
        itemRepository.save(item2);

        final var actualItems = itemService.search("found", 0, 10);

        assertThat(actualItems)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void search_whenTextIsNull_thenReturnedEmptyList() {
        final var actualItems = itemService.search(null, 0, 10);

        assertThat(actualItems)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void search_whenTextIsBlank_thenReturnedEmptyList() {
        final var actualItems = itemService.search("", 0, 10);

        assertThat(actualItems)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void search_whenTextHasDifferentCaseAndSpacesAround_thenReturnedItems() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create items
        final var item = getItem();
        item.setOwnerId(userId);
        item.setDescription("description with keyword -> {found}");
        final var item2 = getItem();
        item2.setOwnerId(userId);
        item2.setName("keyword -> {found}");
        itemRepository.save(item);
        itemRepository.save(item2);

        final var actualItems = itemService.search("   fOuN  ", 0, 10);

        assertThat(actualItems)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    void saveWithoutRequestId_whenInvoked_thenReturnedItem() {
        final var ownerId = userRepository.save(getUser()).getId();
        final var item = getItemCreationDto();

        final var actualItem = itemService.save(ownerId, item);

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", null);
    }

    @Test
    void saveWithRequestId_whenInvoked_thenReturnedItem() {
        // create user
        final var owner = userRepository.save(getUser());
        // create request
        final var request = getItemRequest();
        request.setRequestor(owner);
        final var requestId = itemRequestRepository.save(request).getId();
        // create item
        final var item = getItemCreationDto();
        item.setRequestId(requestId);

        final var actualItem = itemService.save(owner.getId(), item);

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("requestId", requestId);
    }

    @Test
    void saveWithRequestId_whenRequestNotFound_thenNotFoundExceptionThrown() {
        // create user
        final var owner = userRepository.save(getUser());
        // create item
        final var item = getItemCreationDto();
        item.setRequestId(99L);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.save(owner.getId(), item));

        assertThat(exception.getMessage())
                .isEqualTo("Request not found");
    }

    @Test
    void addComment_whenInvoked_thenReturnedComment() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("newemail@t.to");
        userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();
        bookingRepository.save(booking);
        // create comment
        final var comment = getCommentCreationDto();
        comment.setText("Wow, i'm commenting.");

        final var actualComment = itemService.addComment(user.getId(), item.getId(), comment);

        assertThat(actualComment)
                .isNotNull()
                .hasFieldOrPropertyWithValue("text", comment.getText());
    }

    @Test
    void addComment_whenUserNotFound_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("newemail@t.to");
        userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // create booking
        final var booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();
        bookingRepository.save(booking);
        // create comment
        final var comment = getCommentCreationDto();
        comment.setText("Wow, i'm commenting.");

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(99L, item.getId(), comment));

        assertThat(exception.getMessage())
                .isEqualTo("User does not exist");
    }

    @Test
    void addComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        // create users
        userRepository.save(getUser());
        final var user = getUser();
        user.setEmail("newemail@t.to");
        userRepository.save(user);
        // create comment
        final var comment = getCommentCreationDto();
        comment.setText("Wow, i'm commenting.");

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(user.getId(), 99L, comment));

        assertThat(exception.getMessage())
                .isEqualTo("Item not found");
    }

    @Test
    void addComment_whenUserIsNotBooker_thenReturnedComment() {
        // create users
        final var owner = userRepository.save(getUser());
        final var user = getUser();
        user.setEmail("eeemail@to.to");
        userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(owner.getId());
        itemRepository.save(item);
        // create booking
        final var booking = Booking.builder()
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(owner)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .build();
        bookingRepository.save(booking);
        // create comment
        final var comment = getCommentCreationDto();
        comment.setText("Wow, i'm commenting.");

        final var exception = assertThrows(UnavailableException.class,
                () -> itemService.addComment(user.getId(), item.getId(), comment));

        assertThat(exception.getMessage())
                .isEqualTo("You cannot comment on this item");
    }

    @Test
    void update_whenInvoked_thenReturnedItem() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(userId);
        itemRepository.save(item);
        // item to update
        final var itemToUpdate = getItemCreationDto();
        itemToUpdate.setId(item.getId());
        itemToUpdate.setName("updated item");
        itemToUpdate.setAvailable(false);
        itemToUpdate.setDescription("updated desc");

        final var actualItem = itemService.update(item.getId(), userId, itemToUpdate);

        assertThat(actualItem)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", itemToUpdate.getName())
                .hasFieldOrPropertyWithValue("description", itemToUpdate.getDescription())
                .hasFieldOrPropertyWithValue("available", itemToUpdate.getAvailable());
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create item
        final var item = getItem();
        item.setOwnerId(userId);
        itemRepository.save(item);
        // item to update
        final var itemToUpdate = getItemCreationDto();
        itemToUpdate.setId(item.getId());
        itemToUpdate.setName("updated item");

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.update(item.getId(), 99L, itemToUpdate));

        assertThat(exception.getMessage())
                .isEqualTo("User does not exist");
    }

    @Test
    void update_whenItemNotFound_thenNotFoundExceptionThrown() {
        // create users
        final var ownerId = userRepository.save(getUser()).getId();
        final var user = getUser();
        user.setEmail("new@m.to");
        userRepository.save(user);
        // create item
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        // item to update
        final var itemToUpdate = getItemCreationDto();
        itemToUpdate.setId(item.getId());
        itemToUpdate.setName("updated item");

        final var exception = assertThrows(AccessDeniedException.class,
                () -> itemService.update(item.getId(), user.getId(), itemToUpdate));

        assertThat(exception.getMessage())
                .isEqualTo("Access is denied");
    }

    @Test
    void update_whenAccessDenied_thenAccessDeniedExceptionThrown() {
        // create user
        final var userId = userRepository.save(getUser()).getId();
        // create item
        final var itemToUpdate = getItemCreationDto();
        itemToUpdate.setId(99L);
        itemToUpdate.setName("updated item");

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.update(99L, userId, itemToUpdate));

        assertThat(exception.getMessage())
                .isEqualTo("Item not found");
    }

    private User getUser() {
        return User.builder()
                .email("email@t.to")
                .name("user")
                .build();
    }

    private ItemCreationDto getItemCreationDto() {
        return ItemCreationDto.builder()
                .available(true)
                .description("desc")
                .name("item")
                .build();
    }

    private Item getItem() {
        return Item.builder()
                .description("desc")
                .available(true)
                .name("item")
                .build();
    }

    private ItemRequest getItemRequest() {
        return ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("desc")
                .build();
    }

    private CommentCreationDto getCommentCreationDto() {
        return CommentCreationDto.builder()
                .created(LocalDateTime.now())
                .build();
    }
}
