package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Отзыв о вещи
 * id — уникальный идентификатор отзыва;
 * text - текст отзыва;
 * author — автор отзыва;
 * item — вещь, на которую оставили отзыв;
 **/

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @NotEmpty
    private String text;
    @ManyToOne
    private User author;
    @ManyToOne
    private Item item;
    private LocalDateTime created;
}
