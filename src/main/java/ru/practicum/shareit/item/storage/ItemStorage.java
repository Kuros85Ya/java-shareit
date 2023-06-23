package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

public interface ItemStorage {

    HashMap<Integer, Item> getAll();

    Item update(Item item);

    Item save(Item item);

    Item getById(Integer id);
}
