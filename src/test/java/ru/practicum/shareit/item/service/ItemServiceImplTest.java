package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    UserServiceImpl userService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    RequestServiceImpl requestService;

    @InjectMocks
    ItemServiceImpl service;

    @Test
    void getById_positive_withCommentsAndBooking() {
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

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookingRepository.findAllByItem(existingItem)).thenReturn(Arrays.asList(booking));
        when(commentRepository.findCommentByItemEquals(existingItem)).thenReturn(Arrays.asList(commentToItem));

        ItemResponseDto actual = service.getById(itemId, ownerId);

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

        assertEquals(actual, expected);
    }

    @Test
    void getById_NotOwner_withComments_Positive() {
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

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookingRepository.findAllByItem(existingItem)).thenReturn(Arrays.asList(booking));
        when(commentRepository.findCommentByItemEquals(existingItem)).thenReturn(Arrays.asList(commentToItem));

        ItemResponseDto actual = service.getById(itemId, bookerId);

        ItemResponseDto expected = new ItemResponseDto(1,
                existingItem.getName(),
                existingItem.getDescription(),
                existingItem.getAvailable(),
                owner,
                null,
                null,
                null,
                List.of(new CommentResponseDTO(
                        commentToItem.getId(),
                        commentToItem.getText(),
                        booker.getName(),
                        commentToItem.getCreated())
                ));

        assertEquals(actual, expected);
    }

    @Test
    void create_withRequest_positive() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1, "ReqDescription", booker, created);
        Item item = new Item(null, "itemName", "itemDesc", true, owner, request);

        ItemRequestDto itemToCreate = new ItemRequestDto(item.getName(), item.getDescription(), item.getAvailable(), item.getRequest().getId());

        when(userService.getUser(ownerId)).thenReturn(owner);
        when(requestService.getRequest(1)).thenReturn(request);
        when(itemRepository.save(item)).thenReturn(item);

        CreatedItemResponseDto expected = new CreatedItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request.getId()
        );

        CreatedItemResponseDto actual = service.create(ownerId, itemToCreate);
        assertEquals(actual, expected);
    }

    @Test
    void create_withoutRequest_positive() {
        int ownerId = 2;

        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item item = new Item(null, "itemName", "itemDesc", true, owner, null);

        ItemRequestDto itemToCreate = new ItemRequestDto(item.getName(), item.getDescription(), item.getAvailable(), null);

        when(userService.getUser(ownerId)).thenReturn(owner);
        when(itemRepository.save(item)).thenReturn(item);

        CreatedItemResponseDto expected = new CreatedItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null
        );

        CreatedItemResponseDto actual = service.create(ownerId, itemToCreate);
        assertEquals(actual, expected);
    }

    @Test
    void createComment_positive() {
        LocalDateTime startDt = LocalDateTime.now().minusDays(20);
        LocalDateTime endDt = LocalDateTime.now().minusDays(10);
        LocalDateTime created = LocalDateTime.now();
        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        Booking booking = new Booking(1, startDt, endDt, existingItem, booker, Status.WAITING, created);
        Comment commentToItem = new Comment(1, "Comment", booker, existingItem, created.plusDays(2));

        when(userService.getUser(bookerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(bookingRepository.getBookingsByBookerAndItemAndStatusEqualsAndStartBefore(any(), any(), any(), any())).thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(commentToItem);

        CommentDto commentRequest = new CommentDto(commentToItem.getText());
        CommentResponseDTO expected = new CommentResponseDTO(1, commentRequest.getText(), booker.getName(), LocalDateTime.now());

        CommentResponseDTO actual = service.createComment(commentRequest, bookerId, itemId);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getAuthorName(), actual.getAuthorName());
    }

    @Test
    void createComment_negative_didnt_use() {
        LocalDateTime created = LocalDateTime.now();
        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        Comment commentToItem = new Comment(1, "Comment", booker, existingItem, created.plusDays(2));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        CommentDto commentRequest = new CommentDto(commentToItem.getText());
        assertThrows(ValidationException.class, () -> service.createComment(commentRequest, bookerId, itemId));
    }

    @Test
    void update_positive() {

        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);

        ItemDto updatedItem = new ItemDto(itemId, "updatedName", "updatedDescription", false);
        Item expectedItem = new Item(updatedItem.getId(), updatedItem.getName(), updatedItem.getDescription(), updatedItem.getAvailable(), owner, null);

        when(userService.getUser(ownerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        Item actual = service.update(ownerId, updatedItem);
        assertEquals(expectedItem, actual);
    }

    @Test
    void update_NameIsNull() {

        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);

        ItemDto updatedItem = new ItemDto(itemId, null, "updatedDescription", false);
        Item expectedItem = new Item(updatedItem.getId(), existingItem.getName(), updatedItem.getDescription(), updatedItem.getAvailable(), owner, null);

        when(userService.getUser(ownerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        Item actual = service.update(ownerId, updatedItem);
        assertEquals(expectedItem, actual);
    }

    @Test
    void update_DescriptionIsNull() {

        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", null, true, owner, null);

        ItemDto updatedItem = new ItemDto(itemId, "updatedName", null, false);
        Item expectedItem = new Item(updatedItem.getId(), updatedItem.getName(), existingItem.getDescription(), updatedItem.getAvailable(), owner, null);

        when(userService.getUser(ownerId)).thenReturn(booker);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        Item actual = service.update(ownerId, updatedItem);
        assertEquals(expectedItem, actual);
    }

    @Test
    void update_AvailableIsNull() {

        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", null, true, owner, null);

        ItemDto updatedItem = new ItemDto(itemId, "updatedName", "updatedDescription", null);
        Item expectedItem = new Item(updatedItem.getId(), updatedItem.getName(), updatedItem.getDescription(), null, owner, null);

        when(userService.getUser(ownerId)).thenReturn(owner);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(expectedItem);

        Item actual = service.update(ownerId, updatedItem);
        assertEquals(expectedItem, actual);
    }

    @Test
    void update_NotOwner_Exception() {

        int itemId = 1;
        int bookerId = 1;
        int ownerId = 2;

        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", null, true, owner, null);
        ItemDto updatedItem = new ItemDto(itemId, "updatedName", "updatedDescription", null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        verify(itemRepository, never()).save(any());

        assertThrows(NoSuchElementException.class, () -> service.update(bookerId, updatedItem));
    }


    @Test
    void search() {
    }

    @Test
    void getAllUserItems() {
    }

    @Test
    void getItem() {
    }
}