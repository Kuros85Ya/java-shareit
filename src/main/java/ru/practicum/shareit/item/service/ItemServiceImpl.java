package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public List<Item> getAll() {
        return new ArrayList<>(itemStorage.getAll().values());
    }

    @Override
    public Item getById(int id) {
        return itemStorage.getById(id);
    }

    @Override
    public Item create(Integer userId, Item item) {
        User owner = userStorage.getUser(userId);
        item.setOwner(owner);
        return itemStorage.save(item);
    }

    /**
     * Изменить можно название, описание и статус доступа к аренде. Редактировать вещь может только её владелец.
     **/
    @Override
    public Item update(Integer userId, ItemDto item) {
        checkIfUserIsOwner(userId, item.getId());

        Item oldItem = itemStorage.getById(item.getId());

        String name;
        String description;
        Boolean available;

        if (item.getName() != null) name = item.getName();
        else name = oldItem.getName();

        if (item.getDescription() != null) description = item.getDescription();
        else description = oldItem.getDescription();

        if (item.getAvailable() != null) available = item.getAvailable();
        else available = oldItem.getAvailable();

        return itemStorage.update(new Item(item.getId(), name, description, available, userStorage.getUser(userId)));
    }

    @Override
    public Set<Item> search(String query) {
        if (query.isBlank()) {
            return Collections.emptySet();
        } else {
            return itemStorage.getAll().values().stream()
                    .filter(Item::getAvailable)
                    .filter(it -> it.getName().toLowerCase().contains(query.toLowerCase())
                            || it.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public List<Item> getAllUserItems(Integer userId) {
        return itemStorage.getAll().values().stream()
                .filter(it -> it.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    private void checkIfUserIsOwner(Integer userId, Integer itemId) {
        if (!itemStorage.getById(itemId).getOwner().getId().equals(userId))
            throw new NoSuchElementException("Пользователь id " + userId + " не является собственником вещи " + itemId);
    }
}
