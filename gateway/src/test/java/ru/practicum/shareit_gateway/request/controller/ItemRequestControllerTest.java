package ru.practicum.shareit_gateway.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.request.client.ItemRequestClient;
import ru.practicum.shareit_gateway.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit_gateway.request.dto.ItemRequestDto;
import ru.practicum.shareit_gateway.request.dto.ItemRequestInfoDto;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private ItemRequestClient itemRequestClient;

    @SneakyThrows
    @Test
    void findAllRequestsByUserId_thenResponseIsOk() {
        final var requests = List.of(getItemRequestInfoDto());
        when(itemRequestClient.findAllByUserId(anyLong())).thenReturn(ResponseEntity.ok().body(requests));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemRequestClient).findAllByUserId(anyLong());
    }

    @SneakyThrows
    @Test
    void findAllRequests_thenResponseIsOk() {
        final var requests = List.of(getItemRequestInfoDto());
        when(itemRequestClient.findAll(1L, 0, 10)).thenReturn(ResponseEntity.ok().body(requests));

        mockMvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemRequestClient).findAll(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void findRequestById_thenResponseIsOk() {
        final var request = getItemRequestInfoDto();
        when(itemRequestClient.findByRequestId(1L, 2L)).thenReturn(ResponseEntity.ok().body(request));

        final var result = mockMvc.perform(get("/requests/{requestId}", 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(request), result),
                () -> verify(itemRequestClient).findByRequestId(1L, 2L)
        );
    }

    @SneakyThrows
    @Test
    void saveValidRequest_thenResponseIsOk() {
        final var createdRequest = getItemRequestCreationDto();
        createdRequest.setDescription("description");
        final var request = getItemRequestDto();
        request.setDescription("description");
        when(itemRequestClient.save(1L, createdRequest)).thenReturn(ResponseEntity.ok().body(request));

        final var result = mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(request), result),
                () -> verify(itemRequestClient).save(1L, createdRequest)
        );
    }

    private ItemRequestInfoDto getItemRequestInfoDto() {
        return ItemRequestInfoDto.builder().build();
    }

    private ItemRequestDto getItemRequestDto() {
        return ItemRequestDto.builder().build();
    }

    private ItemRequestCreationDto getItemRequestCreationDto() {
        return ItemRequestCreationDto.builder().build();
    }
}
