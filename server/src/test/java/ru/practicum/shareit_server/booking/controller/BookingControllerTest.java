package ru.practicum.shareit_server.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_server.booking.BookingState;
import ru.practicum.shareit_server.booking.dto.BookingCreationDto;
import ru.practicum.shareit_server.booking.dto.BookingDto;
import ru.practicum.shareit_server.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    @SneakyThrows
    @Test
    void findBookingById_thenResponseIsOk() {
        final var booking = getBookingDto();
        when(bookingService.findById(1L, 2L)).thenReturn(booking);

        final var result = mockMvc.perform(get("/bookings/{bookingId}", 2L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(booking), result),
                () -> verify(bookingService).findById(1L, 2L)
        );
    }

    @SneakyThrows
    @Test
    void findAllBookingsByState_thenResponseIsOk() {
        final var bookings = List.of(getBookingDto());
        when(bookingService.findAllByState(1L, BookingState.ALL, 0, 10))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).findAllByState(1L, BookingState.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void findAllByStateForOwner() {
        final var bookings = List.of(getBookingDto());
        when(bookingService.findAllByStateForOwner(1L, BookingState.ALL, 0, 10))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "ALL", 0, 10)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService).findAllByStateForOwner(1L, BookingState.ALL, 0, 10);
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

        when(bookingService.save(1L, createdBooking)).thenReturn(booking);

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
                () -> verify(bookingService).save(1L, createdBooking)
        );
    }

    @SneakyThrows
    @Test
    void approve_thenResponseIsOk() {
        final var booking = getBookingDto();
        when(bookingService.approve(1L, 1L, true)).thenReturn(booking);

        final var result = mockMvc.perform(patch("/bookings/{bookingId}?approved={isApprove}",
                        1L, true)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertAll(
                () -> assertEquals(objectMapper.writeValueAsString(booking), result),
                () -> verify(bookingService).approve(1L, 1L, true)
        );
    }

    private BookingDto getBookingDto() {
        return BookingDto.builder().build();
    }

    private BookingCreationDto getBookingCreationDto() {
        return BookingCreationDto.builder().build();
    }
}
