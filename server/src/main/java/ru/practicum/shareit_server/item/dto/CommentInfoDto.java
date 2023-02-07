package ru.practicum.shareit_server.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentInfoDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
