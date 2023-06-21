package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserRequestDTO;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(int id);

    User create(User user);

    User update(UserRequestDTO user);

    void remove(Integer id);
}
