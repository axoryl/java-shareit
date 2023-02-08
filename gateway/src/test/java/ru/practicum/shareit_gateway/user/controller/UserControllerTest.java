package ru.practicum.shareit_gateway.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.user.client.UserClient;
import ru.practicum.shareit_gateway.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @Test
    void findUserById_thenResponseIsOk() {
        final var user = getUserDto();
        when(userClient.findById(anyLong())).thenReturn(ResponseEntity.ok().body(user));

        final var result = mockMvc.perform(get("/users/{id}", anyLong()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(user), result),
                () -> verify(userClient).findById(anyLong())
        );
    }

    @SneakyThrows
    @Test
    void findAllUsers_thenResponseIsOk() {
        final List<UserDto> expectedUsers = List.of(getUserDto());
        when(userClient.findAll()).thenReturn(ResponseEntity.ok().body(expectedUsers));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(userClient).findAll();
    }

    @SneakyThrows
    @Test
    void saveValidUser_thenResponseIsOk() {
        final var user = getUserDto();
        when(userClient.save(any())).thenReturn(ResponseEntity.ok().body(user));

        final var result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(user), result),
                () -> verify(userClient).save(user)
        );
    }

    @SneakyThrows
    @Test
    void saveNotValidUser_incorrectEmail_thenResponseIsBadRequest() {
        final var userToCreate = getUserDto();
        userToCreate.setEmail("emailmail");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).save(userToCreate);
    }

    @SneakyThrows
    @Test
    void saveNotValidUser_emailIsBlank_thenResponseIsBadRequest() {
        final var userToCreate = getUserDto();
        userToCreate.setEmail("");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).save(userToCreate);
    }

    @SneakyThrows
    @Test
    void saveNotValidUser_nameIsBlank_thenResponseIsBadRequest() {
        final var userToCreate = getUserDto();
        userToCreate.setName("");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToCreate)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).save(userToCreate);
    }

    @SneakyThrows
    @Test
    void updateValidUser_thenResponseIsOk() {
        final var userToUpdate = getUserDto();
        userToUpdate.setName("new_name");
        userToUpdate.setEmail("new_email@m.com");

        when(userClient.update(userToUpdate.getId(), userToUpdate))
                .thenReturn(ResponseEntity.ok().body(userToUpdate));

        final var result = mockMvc.perform(patch("/users/{id}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(userToUpdate), result),
                () -> verify(userClient).update(userToUpdate.getId(), userToUpdate)
        );
    }

    @SneakyThrows
    @Test
    void updateNotValidUser_incorrectEmail_thenResponseIsBadRequest() {
        final var userToUpdate = getUserDto();
        userToUpdate.setName("new_name");
        userToUpdate.setEmail("new_email");

        mockMvc.perform(patch("/users/{id}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(userToUpdate.getId(), userToUpdate);
    }

    @SneakyThrows
    @Test
    void deleteUser_thenResponseIsOk() {
        mockMvc.perform(delete("/users/{id}", anyLong()))
                .andExpect(status().isOk());

        verify(userClient).delete(anyLong());
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("userDto")
                .email("userDto@email.com")
                .build();
    }
}
