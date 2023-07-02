package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserRequestDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService service;

    @SneakyThrows
    @Test
    void when_createUser_normal_then_isCreatedAndBodyReturned() {

        User testUser = new User(1, "test", "test@mail.ru");

        when(service.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testUser.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));

        verify(service).create(testUser);
    }

    @SneakyThrows
    @Test
    void when_createUser_EmailnotValid_then_badRequestNothingCreated() {

        User testUser = new User(1, "name", "notValid");

        when(service.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(testUser);
    }

    @SneakyThrows
    @Test
    void when_createUser_FieldsNotValid_then_badRequestNothingCreated() {

        User testUser = new User(1, null, "test@mail.ru");

        when(service.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(testUser);
    }

    @Test
    @SneakyThrows
    void updateUser() {
        Integer userId = 1;
        UserRequestDTO updatedUser = new UserRequestDTO(
                userId,
                "updatedName",
                "testUpdated@test.ru"
        );

        User expectedUser = new User(userId, updatedUser.getName(), updatedUser.getEmail());

        when(service.update(updatedUser))
                .thenReturn(expectedUser);

        mockMvc.perform(patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUser.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedUser.getName())))
                .andExpect(jsonPath("$.email", is(expectedUser.getEmail())));

        verify(service).update(updatedUser);
    }

    @SneakyThrows
    @Test
    void removeUser() {
        int userId = 1;

        mockMvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).remove(1);
    }

    @SneakyThrows
    @Test
    void getUser() {
        int userId = 1;
        User user = new User(userId, "test", "test@mail.ru");

        when(service.getUser(userId)).thenReturn(user);

        mockMvc.perform(get("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @SneakyThrows
    @Test
    void getAll() {
        int userId = 1;
        User user = new User(userId, "test", "test@mail.ru");

        when(service.getAll()).thenReturn(List.of(user));

        String response = mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAll();
        assertEquals(objectMapper.writeValueAsString(List.of(user)), response);
    }
}