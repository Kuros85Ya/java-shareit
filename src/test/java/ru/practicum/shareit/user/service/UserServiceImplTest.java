package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

//    @InjectMocks
//    private BookingServiceImpl bookingService;

    @Test
    void getUser_whenUserFound_thenUserReturned() {
        Integer userId = 0;
        User user = new User();
        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User response = userService.getUser(0);
        assertEquals(user, response);
    }

    @Test
    void getUser_whenUserNotFound_thenExceptionIsThrown() {
        Integer userId = 0;
        when(repository.findById(userId)).thenReturn((Optional.empty()));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.getUser(0));
    }

    @Test
    void getAll() {
    }

    @Test
    void createUser_whenNameValid_thenSavedUser() {
        User user = new User();
        when(repository.save(user)).thenReturn(user);

        User actual = userService.create(user);
        assertEquals(actual, user);
        verify(repository).save(user);
    }

    @Test
    void createUser_whenNameNotValid_thenNotSavedUser() {
        User user = new User();
        //doThrow(ValidationException.class).when(repository).validate(); //у него тут метод валидации

        //assertThrows(ValidationException.class, () -> userService.create(user));
//        userService.create(user);
//        verify(repository, never()).save(user);
//        verify(repository, times(0)).save(user);
//        verify(repository, atLeast(0)).save(user);
//        verify(repository, atMost(0)).save(user);

    }

//    @Test
//    void findAll_whenInvoked_thenResponseFromService() {
//        List<User> expectedUsers = List.of(new User());
//        Mockito.when(userService.getAll()).thenReturn(expectedUsers);
//
//        List<User> response = userController.findAll();
//        assertEquals(response, expectedUsers);
//    }

    @Test
    void updateUser_whenUserFound_thenUpdatedOnlyAvailableFields() {

    }

    @Test
    void remove() {
    }
}