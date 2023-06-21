package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.dto.UserRequestDTO;

@Component
public class UserMapper {

    public static UserRequestDTO toUserDto(User user) {
        return new UserRequestDTO(
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(Integer id, UserRequestDTO user) {
        return new User(
                id,
                user.getName(),
                user.getEmail()
        );
    }
}
