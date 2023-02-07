package ru.practicum.shareit_server.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_server.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit_server.request.dto.ItemRequestDto;
import ru.practicum.shareit_server.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit_server.request.service.ItemRequestService;

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
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void findAllRequestsByUserId_thenResponseIsOk() {
        final var requests = List.of(getItemRequestInfoDto());
        when(itemRequestService.findAllByUserId(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemRequestService).findAllByUserId(anyLong());
    }

    @SneakyThrows
    @Test
    void findAllRequests_thenResponseIsOk() {
        final var requests = List.of(getItemRequestInfoDto());
        when(itemRequestService.findAllWithPagination(1L, 0, 10))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemRequestService).findAllWithPagination(1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void findRequestById_thenResponseIsOk() {
        final var request = getItemRequestInfoDto();
        when(itemRequestService.findByRequestId(1L, 2L)).thenReturn(request);

        final var result = mockMvc.perform(get("/requests/{requestId}", 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(request), result),
                () -> verify(itemRequestService).findByRequestId(1L, 2L)
        );
    }

    @SneakyThrows
    @Test
    void saveValidRequest_thenResponseIsOk() {
        final var createdRequest = getItemRequestCreationDto();
        createdRequest.setDescription("description");
        final var request = getItemRequestDto();
        request.setDescription("description");
        when(itemRequestService.save(1L, createdRequest)).thenReturn(request);

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
                () -> verify(itemRequestService).save(1L, createdRequest)
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
