package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private final Integer id; // — уникальный идентификатор запроса;
    private final String description; // — текст запроса, содержащий описание требуемой вещи;
    private final User requestor; // — пользователь, создавший запрос;
    private final LocalDateTime created; // — дата и время создания запроса.
}
