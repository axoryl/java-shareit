package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Test
    void findAllRequestsByUserId_whenInvoked_thenReturnedRequests() {
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        itemRequestRepository.save(request);

        final var actualRequests = itemRequestService.findAllByUserId(user.getId());

        assertThat(actualRequests)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void findAllRequestsByUserId_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        itemRequestRepository.save(request);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findByRequestId(99L, request.getId()));

        assertThat(exception.getMessage())
                .isEqualTo("User not found");
    }

    @Test
    void findRequestById_whenInvoked_thenReturnedRequest() {
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        final var expectedRequest = itemRequestRepository.save(request);

        final var actualRequest = itemRequestService.findByRequestId(user.getId(), expectedRequest.getId());

        assertThat(actualRequest)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", expectedRequest.getId())
                .hasFieldOrPropertyWithValue("created", expectedRequest.getCreated())
                .hasFieldOrPropertyWithValue("description", expectedRequest.getDescription());
    }

    @Test
    void findRequestById_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        itemRequestRepository.save(request);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findByRequestId(99L, request.getId()));

        assertThat(exception.getMessage())
                .isEqualTo("User not found");
    }

    @Test
    void findRequestById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        final var user = userRepository.save(getUser());

        final var exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findByRequestId(user.getId(), 99L));

        assertThat(exception.getMessage())
                .isEqualTo("Request not found");
    }

    @Test
    void findAllRequestsWithPagination_whenInvoked_thenReturnedRequests() {
        // create user and request
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        itemRequestRepository.save(request);

        // create requestor and request
        final var requestor = getUser();
        requestor.setEmail("newemail@w.to");
        final var requestorId = userRepository.save(requestor).getId();
        final var request2 = getItemRequest();
        request2.setRequestor(requestor);
        itemRequestRepository.save(request2);

        final var actualRequests = itemRequestService.findAllWithPagination(requestorId, 0, 10);

        assertThat(actualRequests)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    void findAllRequestsWithPagination_whenUserNotFound_thenNotFoundExceptionThrown() {
        final var user = userRepository.save(getUser());
        final var request = getItemRequest();
        request.setRequestor(user);
        itemRequestRepository.save(request);

        final var exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.findAllWithPagination(99L, 0, 10));

        assertThat(exception.getMessage())
                .isEqualTo("User not found");
    }

    @Test
    void saveRequest_whenInvoked_thenReturnedRequest() {
        final var userId = userRepository.save(getUser()).getId();
        final var actualRequest = itemRequestService.save(userId, getItemRequestCreationDto());

        assertThat(actualRequest.getDescription())
                .isEqualTo("desc");
    }

    @Test
    void saveRequest_whenNotValidUserId_thenNotFoundExceptionThrown() {
        final var exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.save(1L, getItemRequestCreationDto()));

        assertThat(exception.getMessage())
                .isEqualTo("User not found");
    }

    private ItemRequestCreationDto getItemRequestCreationDto() {
        return ItemRequestCreationDto.builder()
                .description("desc")
                .build();
    }

    private User getUser() {
        return User.builder()
                .name("user")
                .email("email@t.com")
                .build();
    }

    private ItemRequest getItemRequest() {
        return ItemRequest.builder()
                .created(LocalDateTime.now())
                .description("desc")
                .build();
    }
}
