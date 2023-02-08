package ru.practicum.shareit_server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit_server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIds);
}
