package ru.practicum.shareit_server.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit_server.item.dto.ItemCreationDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestInfoDtoTest {

    @Autowired
    private JacksonTester<ItemRequestInfoDto> json;

    @SneakyThrows
    @Test
    void testItemRequestInfoDto() {
        final var item = ItemCreationDto.builder()
                .id(1L)
                .requestId(1L)
                .available(true)
                .name("name")
                .description("description")
                .build();
        final var request = ItemRequestInfoDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0))
                .items(List.of(item))
                .build();

        JsonContent<ItemRequestInfoDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2000-01-01T00:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description")
                .isEqualTo("description");
    }
}
