package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id; // — уникальный идентификатор вещи;
    private final String name; // — краткое название;
    private final String description; // — развёрнутое описание;
    private final Boolean available; // — статус о том, доступна или нет вещь для аренды;
    private final User owner; // — владелец вещи;
    private final ItemRequest request; // — если вещь была создана по запросу другого польз
}
