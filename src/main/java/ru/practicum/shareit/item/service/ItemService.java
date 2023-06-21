package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {

    Item getById(int id);

    Item create(Integer userId, Item item);

    Item update(Integer userId, ItemDto item);

    Set<Item> search(String query);

    List<Item> getAllUserItems(Integer userId);
}
