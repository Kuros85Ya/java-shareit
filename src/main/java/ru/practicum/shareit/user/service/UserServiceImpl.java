package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User getById(int id) {
        return repository.findById(id).orElseThrow(()
                -> new NoSuchElementException("Пользователь с ID = " + id + " не найден."));
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(UserRequestDTO user) {
        User oldUser = repository.findById(user.getId()).orElseThrow(()
                -> new NoSuchElementException("Пользователь с ID = " + user.getId() + " не найден."));
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

        return repository.save(new User(user.getId(), name, email));
    }

    @Override
    public void remove(Integer id) {
        repository.deleteById(id);
    }
}
