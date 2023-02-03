package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Long currentUserId;

    @BeforeEach
    public void init() {
        currentUserId = initEntity();
    }

    @Test
    void findByCurrentTime() {
        final var pageable = PageRequest.of(0, 20, Sort.unsorted());
        final var actualBookings = bookingRepository.findByCurrentTime(currentUserId,
                LocalDateTime.now(),
                pageable).getContent();

        assertEquals(3, actualBookings.size());
    }

    @Test
    void findByCurrentTime2() {
        final var pageable = PageRequest.of(0, 20, Sort.unsorted());
        final var itemIds = itemRepository.findAll().stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        final var actualBookings = bookingRepository.findByCurrentTime(LocalDateTime.now(),
                itemIds,
                pageable).getContent();

        assertEquals(3, actualBookings.size());
    }

    @AfterEach
    public void deleteAll() {
        deleteEntity();
    }

    private Long initEntity() {
        final var user = userRepository.save(User.builder()
                .name("user")
                .email("email@mail.com")
                .build()
        );

        final var item = itemRepository.save(Item.builder()
                .name("totem")
                .description("ancestral totem")
                .available(true)
                .ownerId(user.getId())
                .build()
        );

        final var item2 = itemRepository.save(Item.builder()
                .name("item 2")
                .description("whatever")
                .available(true)
                .ownerId(user.getId())
                .build()
        );

        bookingRepository.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user)
                .item(item)
                .build()
        );

        bookingRepository.save(Booking.builder()
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user)
                .item(item)
                .build()
        );

        bookingRepository.save(Booking.builder()
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .booker(user)
                .item(item2)
                .build()
        );

        return user.getId();
    }

    private void deleteEntity() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
