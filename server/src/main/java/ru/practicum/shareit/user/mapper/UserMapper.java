package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.model.User;

public class UserMapper {

    public static User toUser(UserRequestDTO user) {
        return new User(user.getId(),
                user.getName(),
                user.getEmail());
    }
}
