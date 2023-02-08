package ru.practicum.shareit_server.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingCreationDtoTest {

    @Autowired
    private JacksonTester<BookingCreationDto> json;

    @SneakyThrows
    @Test
    void testBookingCreationDto() {
        final var booking = BookingCreationDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0))
                .end(LocalDateTime.of(2000, Month.JANUARY, 1, 1, 0, 0))
                .build();

        JsonContent<BookingCreationDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2000-01-01T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2000-01-01T01:00:00");
    }
}
