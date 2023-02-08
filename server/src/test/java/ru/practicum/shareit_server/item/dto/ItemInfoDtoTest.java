package ru.practicum.shareit_server.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit_server.booking.dto.BookingShortDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemInfoDtoTest {
    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @SneakyThrows
    @Test
    void testItemInfoDto() {
        final var lastBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(1L)
                .build();
        final var nextBooking = BookingShortDto.builder()
                .id(2L)
                .bookerId(2L)
                .build();
        final var comment = CommentInfoDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.of(2000, Month.JANUARY, 1, 1, 0, 0))
                .build();
        final var item = ItemInfoDto.builder()
                .id(1L)
                .ownerId(1L)
                .name("name")
                .description("description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();


        JsonContent<ItemInfoDto> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();

        assertThat(result).extractingJsonPathValue("$.lastBooking").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);

        assertThat(result).extractingJsonPathValue("$.nextBooking").isNotNull();
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(2);

        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].authorName")
                .isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].created")
                .isEqualTo("2000-01-01T01:00:00");
    }
}
