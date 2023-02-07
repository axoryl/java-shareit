package ru.practicum.shareit_server.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreationDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
