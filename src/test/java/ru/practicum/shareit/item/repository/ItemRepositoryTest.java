package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init() {
        initEntity();
    }

    @Test
    void search() {
        final var pageable = PageRequest.of(0, 20, Sort.unsorted());
        final var actualItems = itemRepository.search("ances", pageable).getContent();

        assertAll(
                () -> assertEquals(2, actualItems.size()),
                () -> assertTrue(actualItems.get(0).getAvailable(), "item available is false"),
                () -> assertTrue(actualItems.get(1).getAvailable(), "item available is false")
        );
    }

    @AfterEach
    void deleteAll() {
        deleteEntity();
    }

    private void initEntity() {
        final var user = userRepository.save(User.builder()
                .name("user")
                .email("email@mail.com")
                .build()
        );

        itemRepository.save(Item.builder()
                .name("totem")
                .description("ancestral totem")
                .available(true)
                .ownerId(user.getId())
                .build()
        );

        itemRepository.save(Item.builder()
                .name("ancestral")
                .description("whatever")
                .available(true)
                .ownerId(user.getId())
                .build()
        );

        itemRepository.save(Item.builder()
                .name("totem")
                .description("ancestral totem 2")
                .available(false)
                .ownerId(user.getId())
                .build()
        );
    }

    private void deleteEntity() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}
