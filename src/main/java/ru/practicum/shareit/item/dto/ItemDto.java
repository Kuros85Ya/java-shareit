package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class ItemDto {
    public Integer id;
    public String name; // — краткое название;
    public String description; // — развёрнутое описание;
    public Boolean available = true; // — статус о том, доступна или нет вещь для аренды;

    public ItemDto() {
    }

    public ItemDto(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(String name, String description) {
        this.name = name;
        this.description = description;
        this.available = true;
    }

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
