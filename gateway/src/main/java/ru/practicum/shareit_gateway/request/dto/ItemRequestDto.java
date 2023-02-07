package ru.practicum.shareit_gateway.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
}
