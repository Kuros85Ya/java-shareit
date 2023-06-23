package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public List<User> getAll() {
        return storage.getUsers().values().stream().collect(Collectors.toList());
    }

    @Override
    public User getById(int id) {
        return storage.getUser(id);
    }

    @Override
    public User create(User user) {
        return storage.save(user);
    }

    @Override
    public User update(UserRequestDTO user) {
        User oldUser = storage.getUser(user.getId());
        String email;
        String name;

        if (user.getEmail() != null) {
            email = user.getEmail();
        } else {
            email = oldUser.getEmail();
        }

        if (user.getName() != null) {
            name = user.getName();
        } else {
            name = oldUser.getName();
        }

        return storage.update(new User(user.getId(), name, email));
    }

    @Override
    public void remove(Integer id) {
        storage.removeUser(id);
    }
}
