package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {
    public final String name; // — краткое название;
    public final String description; // — развёрнутое описание;
    public final Boolean available; // — статус о том, доступна или нет вещь для аренды;

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
