package ru.practicum.shareit_gateway.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.booking.BookingState;
import ru.practicum.shareit_gateway.booking.client.BookingClient;
import ru.practicum.shareit_gateway.booking.dto.BookingCreationDto;
import ru.practicum.shareit_gateway.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private BookingClient bookingClient;

    @SneakyThrows
    @Test
    void findBookingById_thenResponseIsOk() {
        final var booking = getBookingDto();
        when(bookingClient.findById(1L, 2L)).thenReturn(ResponseEntity.ok().body(booking));

        final var result = mockMvc.perform(get("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(booking), result),
                () -> verify(bookingClient).findById(1L, 2L)
        );
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState_thenResponseIsOk() {
        final var bookings = List.of(getBookingDto());
        when(bookingClient.findAllByState(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok().body(bookings));

        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingClient).findAllByState(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState_withDefaultValue_thenResponseIsOk() {
        final var bookings = List.of(getBookingDto());
        when(bookingClient.findAllByState(1L, BookingState.ALL, 0, 20))
                .thenReturn(ResponseEntity.ok().body(bookings));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingClient).findAllByState(1L, BookingState.ALL, 0, 20);
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState_withoutHeaderUserId_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", "ALL", 0, 10))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByState(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllBookingsByNotValidState_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings?state={state}", "whatever")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByState(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByStateForOwner() {
        final var bookings = List.of(getBookingDto());
        when(bookingClient.findAllByStateForOwner(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok().body(bookings));

        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingClient).findAllByStateForOwner(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void findAllByStateForOwner_withDefaultValue_thenResponseIsOk() {
        final var bookings = List.of(getBookingDto());
        when(bookingClient.findAllByStateForOwner(1L, BookingState.ALL, 0, 20))
                .thenReturn(ResponseEntity.ok().body(bookings));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingClient).findAllByStateForOwner(1L, BookingState.ALL, 0, 20);
    }

    @SneakyThrows
    @Test
    void findAllByStateForOwner_withoutHeaderUserId_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", 0, 10))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByStateForOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void findAllByNotValidStateForOwner_thenResponseIsBadRequest() {
        mockMvc.perform(get("/bookings/owner?state={state}", "whatever")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).findAllByStateForOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void saveValidBooking_thenResponseIsOk() {
        final var start = LocalDateTime.now().plusMinutes(5);
        final var end = LocalDateTime.now().plusHours(1);
        final var createdBooking = getBookingCreationDto();
        createdBooking.setItemId(1L);
        createdBooking.setStart(start);
        createdBooking.setEnd(end);
        final var booking = getBookingDto();
        booking.setStart(start);
        booking.setEnd(end);

        when(bookingClient.save(1L, createdBooking)).thenReturn(ResponseEntity.ok().body(booking));

        final var result = mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdBooking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(booking), result),
                () -> verify(bookingClient).save(1L, createdBooking)
        );
    }

    @SneakyThrows
    @Test
    void saveNotValidBooking_itemIdIsNull_thenResponseIsBadRequest() {
        final var start = LocalDateTime.now().plusMinutes(5);
        final var end = LocalDateTime.now().plusHours(1);
        final var createdBooking = getBookingCreationDto();
        createdBooking.setStart(start);
        createdBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdBooking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void saveNotValidBooking_incorrectStart_thenResponseIsBadRequest() {
        final var start = LocalDateTime.now().minusMinutes(5);
        final var end = LocalDateTime.now().plusHours(1);
        final var createdBooking = getBookingCreationDto();
        createdBooking.setItemId(1L);
        createdBooking.setStart(start);
        createdBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdBooking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void saveNotValidBooking_incorrectEnd_thenResponseIsBadRequest() {
        final var start = LocalDateTime.now().plusMinutes(5);
        final var end = LocalDateTime.now();
        final var createdBooking = getBookingCreationDto();
        createdBooking.setItemId(1L);
        createdBooking.setStart(start);
        createdBooking.setEnd(end);

        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createdBooking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).save(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void approve_thenResponseIsOk() {
        final var booking = getBookingDto();
        when(bookingClient.approve(1L, 1L, true)).thenReturn(ResponseEntity.ok().body(booking));

        final var result = mockMvc.perform(patch("/bookings/{bookingId}?approved={isApprove}",
                        1L, true)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(booking), result),
                () -> verify(bookingClient).approve(1L, 1L, true)
        );
    }

    private BookingDto getBookingDto() {
        return BookingDto.builder().build();
    }

    private BookingCreationDto getBookingCreationDto() {
        return BookingCreationDto.builder().build();
    }
}
