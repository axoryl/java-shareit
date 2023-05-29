package ru.practicum.shareit_server.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemShortDto {

    private Long id;
    private String name;
}