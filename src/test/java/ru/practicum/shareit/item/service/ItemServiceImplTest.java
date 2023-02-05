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

import static org.junit.jupiter.api.Assertions.*;

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

        assertAll(
                () -> assertNotNull(actualItem, "Item is null"),
                () -> assertNotNull(actualItem.getComments(), "Comments is null"),
                () -> assertNull(actualItem.getLastBooking(), "LastBooking must be null"),
                () -> assertNull(actualItem.getNextBooking(), "NextBooking must be null"),
                () -> assertEquals(0, actualItem.getComments().size()),
                () -> assertEquals(item.getName(), actualItem.getName()),
                () -> assertEquals(item.getDescription(), actualItem.getDescription()),
                () -> assertEquals(item.getOwnerId(), actualItem.getOwnerId())
        );
    }

    @Test
    void findItemById_whenItemNotFound_thenNotFoundExceptionThrown() {
        final var userId = userRepository.save(getUser()).getId();

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(userId, 99L));

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void findItemById_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var userId = userRepository.save(getUser()).getId();
        final var item = getItem();
        item.setOwnerId(userId);
        itemRepository.save(item);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findById(99L, item.getId()));

        assertEquals("User does not exist", exception.getMessage());
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

        assertAll(
                () -> assertNotNull(actualItem),
                () -> assertNotNull(actualItem.getLastBooking()),
                () -> assertNotNull(actualItem.getNextBooking()),
                () -> assertNotNull(actualItem.getComments()),
                () -> assertEquals(lastBooking.getId(), actualItem.getLastBooking().getId()),
                () -> assertEquals(nextBooking.getId(), actualItem.getNextBooking().getId()),
                () -> assertEquals(0, actualItem.getComments().size()),
                () -> assertEquals(item.getName(), actualItem.getName()),
                () -> assertEquals(item.getDescription(), actualItem.getDescription()),
                () -> assertEquals(item.getOwnerId(), actualItem.getOwnerId())
        );
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

        assertAll(
                () -> assertNotNull(actualItem),
                () -> assertNotNull(actualItem.getComments()),
                () -> assertEquals(2, actualItem.getComments().size()),
                () -> assertEquals(item.getName(), actualItem.getName()),
                () -> assertEquals(item.getDescription(), actualItem.getDescription()),
                () -> assertEquals(item.getOwnerId(), actualItem.getOwnerId())
        );
    }

    @Test
    void findAllOwnerItems_whenInvoked_thenReturnedItems() {
        final var ownerId = userRepository.save(getUser()).getId();
        final var item = getItem();
        item.setOwnerId(ownerId);
        itemRepository.save(item);

        final var actualItems = itemService.findAllOwnerItems(ownerId, 0, 10);

        assertAll(
                () -> assertNotNull(actualItems),
                () -> assertEquals(1, actualItems.size()),
                () -> assertEquals(item.getId(), actualItems.get(0).getId()),
                () -> assertEquals(item.getName(), actualItems.get(0).getName()),
                () -> assertEquals(item.getDescription(), actualItems.get(0).getDescription())
        );
    }

    @Test
    void findAllOwnerItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> itemService.findAllOwnerItems(99L, 0, 10));

        assertEquals("User does not exist", exception.getMessage());
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

        assertAll(
                () -> assertNotNull(actualItems),
                () -> assertEquals(2, actualItems.size())
        );
    }

    @Test
    void search_whenTextIsNull_thenReturnedEmptyList() {
        final var actualItems = itemService.search(null, 0, 10);

        assertAll(
                () -> assertNotNull(actualItems),
                () -> assertEquals(0, actualItems.size())
        );
    }

    @Test
    void search_whenTextIsBlank_thenReturnedEmptyList() {
        final var actualItems = itemService.search("", 0, 10);

        assertAll(
                () -> assertNotNull(actualItems),
                () -> assertEquals(0, actualItems.size())
        );
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

        assertAll(
                () -> assertNotNull(actualItems),
                () -> assertEquals(2, actualItems.size())
        );
    }

    @Test
    void saveWithoutRequestId_whenInvoked_thenReturnedItem() {
        final var ownerId = userRepository.save(getUser()).getId();
        final var item = getItemCreationDto();

        final var actualItem = itemService.save(ownerId, item);

        assertAll(
                () -> assertNotNull(actualItem),
                () -> assertEquals(item.getName(), actualItem.getName()),
                () -> assertEquals(item.getDescription(), actualItem.getDescription()),
                () -> assertNull(actualItem.getRequestId())
        );
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

        assertAll(
                () -> assertNotNull(actualItem),
                () -> assertEquals(item.getName(), actualItem.getName()),
                () -> assertEquals(item.getDescription(), actualItem.getDescription()),
                () -> assertEquals(requestId, actualItem.getRequestId())
        );
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

        assertEquals("Request not found", exception.getMessage());
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

        assertAll(
                () -> assertNotNull(actualComment),
                () -> assertEquals(comment.getText(), actualComment.getText())
        );
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

        assertEquals("User does not exist", exception.getMessage());
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

        assertEquals("Item not found", exception.getMessage());
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

        assertEquals("You cannot comment on this item", exception.getMessage());
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

        assertAll(
                () -> assertNotNull(actualItem),
                () -> assertEquals(itemToUpdate.getName(), actualItem.getName()),
                () -> assertEquals(itemToUpdate.getDescription(), actualItem.getDescription()),
                () -> assertEquals(itemToUpdate.getAvailable(), actualItem.getAvailable())
        );
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

        assertEquals("User does not exist", exception.getMessage());
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

        assertEquals("Access is denied", exception.getMessage());
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

        assertEquals("Item not found", exception.getMessage());
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
