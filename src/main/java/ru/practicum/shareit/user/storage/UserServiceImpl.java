package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.User;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    public List<User> getAll() {
        return new ArrayList<>(storage.getUsers().values());
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
    public User update(User user) {
        return storage.update(user);
    }

    @Override
    public void remove(Integer id) {
        storage.removeUser(id);
    }
}
