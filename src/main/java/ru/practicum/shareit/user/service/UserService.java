package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAll();

    User getById(int id);

    User create(User user);

    User update(User user);

    void remove(Integer id);
}
