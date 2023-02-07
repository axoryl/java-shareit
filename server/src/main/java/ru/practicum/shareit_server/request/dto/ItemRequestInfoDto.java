package ru.practicum.shareit_server.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit_server.item.dto.ItemCreationDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestInfoDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemCreationDto> items;
}
