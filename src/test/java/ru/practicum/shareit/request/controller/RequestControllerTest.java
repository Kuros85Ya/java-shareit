package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.dto.RequestedItemResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    RequestService service;

    @SneakyThrows
    @Test
    void create_whenAllFieldsValid_thenRequestIsCreated() {
        Integer ownerId = 1;
        User owner = new User(1, "test", "test@mail.ru");
        LocalDateTime created = LocalDateTime.now();

        RequestRequestDTO requestDto = new RequestRequestDTO("desc");

        Request expected = new Request(1,
                requestDto.getDescription(),
                owner,
                created);

        when(service.create(ownerId, requestDto)).thenReturn(expected);

        String actual = mockMvc.perform(post("/requests")
                        .header(OWNER_ID_HEADER, ownerId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(service).create(ownerId, requestDto);
    }

    @SneakyThrows
    @Test
    void getResponsesToUserRequest_whenResponsesArePresent_thenTheyAreReturned() {
        Integer requestorId = 1;
        User requestor = new User(requestorId, "requestor", "requestor@mail.ru");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        Integer ownerId = 1;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1,
                "desc1",
                requestor,
                created);

        Item createdItem = new Item(1,
                "itemName",
                "itemDesc",
                true,
                owner,
                request);

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        when(service.getItemsThatWereCreatedByRequest(requestorId)).thenReturn(List.of(expected));

        String actual = mockMvc.perform(get("/requests")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(expected)), actual);
        verify(service).getItemsThatWereCreatedByRequest(requestorId);
    }

    @SneakyThrows
    @Test
    void getAllRequestsPageable_whenAllRequestsAreRequested_thenAllOnPageAreReturned() {
        Integer requestorId = 1;
        User requestor = new User(requestorId, "requestor", "requestor@mail.ru");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        Integer ownerId = 2;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1,
                "desc1",
                requestor,
                created);
        Item createdItem = new Item(1,
                "itemName",
                "itemDesc",
                true,
                owner,
                request);

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        when(service.getAllRequestsPageable(requestorId, 0, 10)).thenReturn(List.of(expected));

        String actual = mockMvc.perform(get("/requests/all")
                        .header(OWNER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(expected)), actual);
        verify(service).getAllRequestsPageable(requestorId, 0, 10);
    }

    @SneakyThrows
    @Test
    void getSingleRequestItems_whenAllItemsByRequestArePresent_thenTheyAreReturned() {
        Integer requestorId = 1;
        User requestor = new User(requestorId, "requestor", "requestor@mail.ru");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        Integer ownerId = 2;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1,
                "desc1",
                requestor,
                created);

        Item createdItem = new Item(1,
                "itemName",
                "itemDesc",
                true,
                owner,
                request);

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        when(service.getSingleRequestById(request.getId(), requestorId)).thenReturn(expected);

        String actual = mockMvc.perform(get("/requests/" + request.getId())
                        .header(OWNER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(service).getSingleRequestById(request.getId(), requestorId);
    }
}