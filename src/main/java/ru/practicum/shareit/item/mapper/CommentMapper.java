package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static Comment mapToComment(User user, Item item, CommentCreationDto commentCreationDto) {
        return Comment.builder()
                .text(commentCreationDto.getText())
                .author(user)
                .item(item)
                .created(commentCreationDto.getCreated())
                .build();
    }

    public static CommentInfoDto mapToCommentInfoDto(Comment comment) {
        return CommentInfoDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentInfoDto> mapToCommentInfoDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapToCommentInfoDto)
                .collect(Collectors.toList());
    }
}
