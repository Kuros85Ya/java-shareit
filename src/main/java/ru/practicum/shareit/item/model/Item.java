package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id; // — уникальный идентификатор вещи;
    @NotEmpty
    private String name; // — краткое название;
    @NotEmpty
    private String description; // — развёрнутое описание;
    @NotNull
    private Boolean available; // — статус о том, доступна или нет вещь для аренды;
    private User owner; // — владелец вещи;
    private ItemRequest request; // — если вещь была создана по запросу другого польз

    public Item() {
    }

    public Item(Integer id, String name, String description, Boolean available, User owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }

    public Item(String name, String description, Boolean available, User owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
