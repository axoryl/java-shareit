package ru.practicum.shareit_gateway.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentCreationDto {

    private String text;
    private LocalDateTime created;
}
