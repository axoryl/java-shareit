package ru.practicum.shareit.item.dto;

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
public class CommentCreationDtoTest {

    @Autowired
    private JacksonTester<CommentCreationDto> json;

    @SneakyThrows
    @Test
    void testCommentCreationDto() {
        final var comment = CommentCreationDto.builder()
                .text("text")
                .created(LocalDateTime.of(2000, Month.JANUARY, 1, 1, 0, 0))
                .build();

        JsonContent<CommentCreationDto> result = json.write(comment);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2000-01-01T01:00:00");
    }
}
