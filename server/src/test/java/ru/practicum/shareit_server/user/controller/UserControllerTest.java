package ru.practicum.shareit_server.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_server.user.dto.UserDto;
import ru.practicum.shareit_server.user.service.UserService;

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
    private UserService userService;

    @SneakyThrows
    @Test
    void findUserById_thenResponseIsOk() {
        final var user = getUserDto();
        when(userService.findById(anyLong())).thenReturn(user);

        final var result = mockMvc.perform(get("/users/{id}", anyLong()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(user), result),
                () -> verify(userService).findById(anyLong())
        );
    }

    @SneakyThrows
    @Test
    void findAllUsers_thenResponseIsOk() {
        final List<UserDto> expectedUsers = List.of(getUserDto());
        when(userService.findAll()).thenReturn(expectedUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(userService).findAll();
    }

    @SneakyThrows
    @Test
    void saveValidUser_thenResponseIsOk() {
        final var user = getUserDto();
        when(userService.save(any())).thenReturn(user);

        final var result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(user), result),
                () -> verify(userService).save(user)
        );
    }

    @SneakyThrows
    @Test
    void updateValidUser_thenResponseIsOk() {
        final var userToUpdate = getUserDto();
        userToUpdate.setName("new_name");
        userToUpdate.setEmail("new_email@m.com");

        when(userService.update(userToUpdate.getId(), userToUpdate)).thenReturn(userToUpdate);

        final var result = mockMvc.perform(patch("/users/{id}", userToUpdate.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(userToUpdate), result),
                () -> verify(userService).update(userToUpdate.getId(), userToUpdate)
        );
    }

    @SneakyThrows
    @Test
    void deleteUser_thenResponseIsOk() {
        mockMvc.perform(delete("/users/{id}", anyLong()))
                .andExpect(status().isOk());

        verify(userService).deleteById(anyLong());
    }

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("userDto")
                .email("userDto@email.com")
                .build();
    }
}
