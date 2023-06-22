package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

public interface UserStorage {

    HashMap<Integer, User> getUsers();

    User update(User user);

    User save(User user);

    void removeUser(Integer id);

    User getUser(Integer id);
}
