package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

public interface ItemStorage {

    HashMap<Integer, Item> getAll();

    Item update(Item user);

    Item save(Item user);

    void remove(Integer id);

    Item getById(Integer id);
}
