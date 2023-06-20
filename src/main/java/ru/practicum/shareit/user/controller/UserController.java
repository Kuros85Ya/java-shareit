package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) {
        log.info("Создаем пользователя: {}", user);
        return service.create(user);
    }

    @PostMapping("/{userId}")
    public User updateUser(@RequestBody @Valid User user) {
        log.info("Создаем пользователя: {}", user);
        return service.create(user);
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Вывести всех пользователей");
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable int userId) {
        log.info("Вывести пользователя ID = {}", userId);
        return service.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable int userId) {
        service.remove(userId);
    }
}

