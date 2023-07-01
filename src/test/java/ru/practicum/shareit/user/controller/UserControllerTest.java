package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import static org.hamcrest.Matchers.is;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository repository;

    @MockBean
    UserService userService;

//    @Test
    @SneakyThrows
    void updateUser() {
        User testUser = new User(1, "test", "test@mail.ru");
        User testUserUpdated = new User(1, "test", "test2@mail.ru");

        when(userService.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(patch("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(testUser.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));

        verify(userService).create(testUser);
    }

    @SneakyThrows
    @Test
    void when_createUser_normal_then_isCreatedAndBodyReturned() {

        User testUser = new User(1, "test", "test@mail.ru");

        when(userService.create(testUser))
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

        verify(userService).create(testUser);
    }

    @SneakyThrows
    @Test
    void when_createUser_EmailnotValid_then_badRequestNothingCreated() {

        User testUser = new User(1, "name", "notValid");

        when(userService.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(testUser);
    }

    @SneakyThrows
    @Test
    void when_createUser_FieldsNotValid_then_badRequestNothingCreated() {

        User testUser = new User(1, null, "test@mail.ru");

        when(userService.create(testUser))
                .thenReturn(testUser);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(testUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(testUser);
    }
//
//        Integer userId = 0;
////        mvc.perform(post(""))
//
//        mvc.perform(get("/"))
//                .andExpect(status().isOk());
//
//        verify(userService).getAll();

    @Test
    void removeUser() {
    }
}