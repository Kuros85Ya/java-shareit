package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getUser_whenUserFound_thenUserReturned() {
        Integer userId = 0;
        User user = new User();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User response = service.getUser(0);
        assertEquals(user, response);
    }

    @Test
    void getUser_whenUserNotFound_thenExceptionIsThrown() {
        Integer userId = 0;
        when(repository.findById(userId)).thenReturn((Optional.empty()));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> service.getUser(0));
    }

    @Test
    void getAll_positive() {
        Integer userId = 1;
        User user = new User(userId,
                "Name",
                "test@test.ru");

        when(repository.findAll()).thenReturn(List.of(user));

        List<User> actual = service.getAll();
        assertEquals(actual, List.of(user));
    }

    @Test
    void createUser_whenNameValid_thenSavedUser() {
        User user = new User();
        when(repository.save(user)).thenReturn(user);

        User actual = service.create(user);
        assertEquals(actual, user);
        verify(repository).save(user);
    }

    @Test
    void update_positive() {
        Integer userId = 1;
        User user = new User(userId,
                "Name",
                "test@test.ru");

        UserRequestDTO updatedUser = new UserRequestDTO(
                userId,
                "updatedName",
                "testUpdated@test.ru"
        );
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User expected = new User(userId,
                updatedUser.getName(),
                updatedUser.getEmail());

        when(repository.save(expected)).thenReturn(expected);
        User actual = service.update(updatedUser);

        assertEquals(actual, expected);
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedOnlyAvailableFieldsEmail() {
        Integer userId = 1;
        User user = new User(userId,
                "Name",
                "test@test.ru");

        UserRequestDTO updatedUser = new UserRequestDTO(
                userId,
                null,
                "testUpdated@test.ru"
        );
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User expected = new User(userId,
                user.getName(),
                updatedUser.getEmail());

        when(repository.save(expected)).thenReturn(expected);
        User actual = service.update(updatedUser);

        assertEquals(actual, expected);
    }

    @Test
    void updateUser_whenUserFound_thenUpdatedOnlyAvailableFieldsName() {
        Integer userId = 1;
        User user = new User(userId,
                "Name",
                "test@test.ru");

        UserRequestDTO updatedUser = new UserRequestDTO(
                userId,
                "updated",
                null
        );
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User expected = new User(userId,
                updatedUser.getName(),
                user.getEmail());

        when(repository.save(expected)).thenReturn(expected);
        User actual = service.update(updatedUser);

        assertEquals(actual, expected);
    }

    @Test
    void remove() {
        assertDoesNotThrow(() -> service.remove(1));
    }
}