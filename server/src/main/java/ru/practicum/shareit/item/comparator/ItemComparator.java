package ru.practicum.shareit.item.comparator;

import ru.practicum.shareit.item.model.Item;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {
    @Override
    public int compare(Item item1, Item item2) {
        return item1.getId().compareTo(item2.getId());
    }
}
