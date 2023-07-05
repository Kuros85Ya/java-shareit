package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentResponseDTO toCommentResponseDTO(Comment comment, User user) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getText(),
                user.getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDto comment, User user, Item item) {
        return new Comment(
                null,
                comment.getText(),
                user,
                item,
                LocalDateTime.now());
    }
}
