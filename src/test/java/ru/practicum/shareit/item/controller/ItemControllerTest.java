package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ItemService service;

    @SneakyThrows
    @Test
    void create() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1, "ReqDescription", booker, created);
        Item item = new Item(null, "itemName", "itemDesc", true, owner, request);

        ItemRequestDto itemToCreate = new ItemRequestDto(item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId());

        CreatedItemResponseDto expected = new CreatedItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request.getId()
        );

        when(service.create(ownerId, itemToCreate)).thenReturn(expected);

        String actual = mockMvc.perform(post("/items")
                        .header(OWNER_ID_HEADER, ownerId)
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).create(ownerId, itemToCreate);
        assertEquals(objectMapper.writeValueAsString(expected), actual);
    }

    @SneakyThrows
    @Test
    void create_noOwnerHeader() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1, "ReqDescription", booker, created);
        Item item = new Item(null, "itemName", "itemDesc", true, owner, request);

        ItemRequestDto itemToCreate = new ItemRequestDto(item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId());

        CreatedItemResponseDto expected = new CreatedItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request.getId()
        );

        when(service.create(ownerId, itemToCreate)).thenReturn(expected);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(ownerId, itemToCreate);
    }

    @SneakyThrows
    @Test
    void update_positive() {
        int itemId = 1;
        int ownerId = 2;

        User owner = new User(ownerId, "owner", "owner@mail.ru");

        ItemDto updatedItem = new ItemDto(itemId, "updatedName", "updatedDescription", false);
        Item expectedItem = new Item(updatedItem.getId(), updatedItem.getName(), updatedItem.getDescription(), updatedItem.getAvailable(), owner, null);

        when(service.update(ownerId, updatedItem)).thenReturn(expectedItem);

        mockMvc.perform(patch("/items/" + itemId)
                        .header(OWNER_ID_HEADER, ownerId)
                        .content(objectMapper.writeValueAsString(updatedItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItem.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(expectedItem.getName())))
                .andExpect(jsonPath("$.description", is(expectedItem.getDescription())));

        verify(service).update(ownerId, updatedItem);
    }

    @SneakyThrows
    @Test
    void createComment_positive() {
        Integer itemId = 1;
        Integer bookerId = 1;
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentRequest = new CommentDto("test");
        CommentResponseDTO expected = new CommentResponseDTO(1, commentRequest.getText(), "booker", created);

        when(service.createComment(commentRequest, 1, 1)).thenReturn(expected);

        String actual = mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header(OWNER_ID_HEADER, bookerId)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(service).createComment(commentRequest, bookerId, itemId);
    }

    @SneakyThrows
    @Test
    void getById() {
        LocalDateTime startDt = LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();
        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        Booking booking = new Booking(1, startDt, endDt, existingItem, booker, Status.WAITING, created);
        Comment commentToItem = new Comment(1, "Comment", booker, existingItem, created.plusDays(2));

        ItemResponseDto expected = new ItemResponseDto(1,
                existingItem.getName(),
                existingItem.getDescription(),
                existingItem.getAvailable(),
                owner,
                null,
                null,
                new BookingInfoDto(1, 1),
                List.of(new CommentResponseDTO(
                        commentToItem.getId(),
                        commentToItem.getText(),
                        booker.getName(),
                        commentToItem.getCreated())
                ));

        when(service.getById(itemId, ownerId)).thenReturn(expected);

        String actual = mockMvc.perform(get("/items/" + itemId)
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(service).getById(itemId, ownerId);

    }

    @SneakyThrows
    @Test
    void getUserItems() {
        Integer ownerId = 1;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        ItemResponseDto expected = new ItemResponseDto(1,
                "name",
                "desc",
                true,
                owner,
                null,
                null,
                new BookingInfoDto(1, 1),
                List.of());

        when(service.getAllUserItems(ownerId, 0, 10)).thenReturn(List.of(expected));

        String actual = mockMvc.perform(get("/items")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(expected)), actual);
        verify(service).getAllUserItems(ownerId, 0, 10);
    }

    @SneakyThrows
    @Test
    void testSearch_withPageRequestParameters() {
        String query = "name";

        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1, "ReqDescription", booker, created);
        Item item = new Item(1, "itemName", "itemDesc", true, owner, request);

        when(service.search(query, 1, 1)).thenReturn(List.of(item));

        String actual = mockMvc.perform(get("/items/search?text=" + query + "&from=1&size=1")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(item)), actual);
        verify(service).search(query, 1, 1);
    }
}