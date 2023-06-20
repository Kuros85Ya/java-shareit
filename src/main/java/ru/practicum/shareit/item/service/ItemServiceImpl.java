package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    public List<Item> getAll() {
        return new ArrayList<>(itemStorage.getAll().values());
    }

    @Override
    public Item getById(int id) {
        return itemStorage.getById(id);
    }

    @Override
    public Item create(Item item) {
        return itemStorage.save(item);
    }

    @Override
    public Item update(Item item) {
        return itemStorage.update(item);
    }

    @Override
    public void remove(Integer id) {
        itemStorage.remove(id);
    }
}
