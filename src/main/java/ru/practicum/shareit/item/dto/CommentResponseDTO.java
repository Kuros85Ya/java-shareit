package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Integer id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
