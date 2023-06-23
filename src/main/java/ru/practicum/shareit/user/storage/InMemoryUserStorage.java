package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    private Integer generatorId = 0;

    public int generateId() {
        return ++generatorId;
    }

    private void checkIfEmailIsUnique(String email, Integer id) {
        if (users.values().stream().filter(it -> !it.getId().equals(id)).anyMatch(it -> it.getEmail().equals(email)))
            throw new ValidationException("Пользователь с таким email" + email + " уже существует");
    }

    private void checkIfUserExists(Integer id) {
        if (users.get(id) == null) throw new NoSuchElementException("Пользователя с id " + id + " не существует");
    }


    @Override
    public User save(User user) {
        checkIfEmailIsUnique(user.getEmail(), null);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUser(Integer id) {
        checkIfUserExists(id);
        users.remove(id);
    }

    @Override
    public User getUser(Integer id) {
        checkIfUserExists(id);
        return users.get(id);
    }

    @Override
    public User update(User user) {
        checkIfUserExists(user.getId());
        checkIfEmailIsUnique(user.getEmail(), user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
