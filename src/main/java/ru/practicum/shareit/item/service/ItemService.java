package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAll();

    Item getById(int id);

    Item create(Item item);

    Item update(Item item);

    void remove(Integer id);
}
