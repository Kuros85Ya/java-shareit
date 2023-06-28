package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.NoSuchElementException;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Item> items = new HashMap<>();

    private Integer generatorId = 0;

    private int generateId() {
        return ++generatorId;
    }

    private void checkIfItemExists(Integer id) {
        if (items.get(id) == null) throw new NoSuchElementException("Вещи с id " + id + " не существует");
    }

    @Override
    public Item save(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public HashMap<Integer, Item> getAll() {
        return items;
    }

    @Override
    public Item getById(Integer id) {
        checkIfItemExists(id);
        return items.get(id);
    }

    @Override
    public Item update(Item item) {
        checkIfItemExists(item.getId());
        items.put(item.getId(), item);
        return item;
    }
}
