package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @SneakyThrows
    @Test
    void testBookingDtoDto() {
        final var user = UserShortDto.builder()
                .id(1L)
                .build();
        final var item = ItemShortDto.builder()
                .id(1L)
                .name("name")
                .build();
        final var booking = BookingDto.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0))
                .end(LocalDateTime.of(2000, Month.JANUARY, 1, 1, 0, 0))
                .booker(user)
                .item(item)
                .build();

        JsonContent<BookingDto> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2000-01-01T00:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2000-01-01T01:00:00");

        assertThat(result).extractingJsonPathValue("$.booker").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);

        assertThat(result).extractingJsonPathValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("name");
    }
}
