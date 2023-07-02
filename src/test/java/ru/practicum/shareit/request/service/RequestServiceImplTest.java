package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.dto.RequestedItemResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    UserServiceImpl userService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    RequestRepository requestRepository;

    @InjectMocks
    RequestServiceImpl service;

    @Test
    void create_positive() {
        Integer ownerId = 1;
        User owner = new User(1, "test", "test@mail.ru");

        RequestRequestDTO requestDto = new RequestRequestDTO("desc");

        Request expected = new Request(1,
                requestDto.getDescription(),
                owner,
                LocalDateTime.now());

        when(userService.getUser(ownerId)).thenReturn(owner);
        when(requestRepository.save(any())).thenReturn(expected);

        Request actual = service.create(ownerId, requestDto);
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getDescription(), expected.getDescription());
        assertEquals(actual.getRequestor(), expected.getRequestor());
    }

    @Test
    void getItemsThatWereCreatedByRequest_positive() {
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

        when(userService.getUser(requestorId)).thenReturn(requestor);
        when(requestRepository.findAll()).thenReturn(Arrays.asList(request));
        when(itemRepository.findAllByRequest(request)).thenReturn(List.of(createdItem));

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        List<RequestResponseDTO> actual = service.getItemsThatWereCreatedByRequest(requestorId);
        assertEquals(List.of(expected), actual);
    }

    @Test
    void getAllRequestsPageable_positive_not_owner() {
        PageRequest pageRequest = PageRequest.of(0, 10);
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

        when(requestRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(request)));
        when(itemRepository.findAllByRequest(request)).thenReturn(List.of(createdItem));

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        List<RequestResponseDTO> actual = service.getAllRequestsPageable(ownerId, 0, 10);
        assertEquals(List.of(expected), actual);
    }

    @Test
    void getAllRequestsPageable_positive_owner_getsEmptyList() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer requestorId = 1;
        User requestor = new User(requestorId, "requestor", "requestor@mail.ru");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        Integer ownerId = 2;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1,
                "desc1",
                requestor,
                created);

        when(requestRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(request)));

        List<RequestResponseDTO> actual = service.getAllRequestsPageable(requestorId, 0, 10);
        assertEquals(List.of(), actual);
    }

    @Test
    void getSingleRequestById_positive() {
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

        when(userService.getUser(requestorId)).thenReturn(requestor);
        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequest(request)).thenReturn(List.of(createdItem));

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        RequestResponseDTO expected = new RequestResponseDTO(request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(expectedItem));

        RequestResponseDTO actual = service.getSingleRequestById(1, requestorId);
        assertEquals(expected, actual);
    }

    @Test
    void getSingleRequestById_negative() {
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

        when(userService.getUser(requestorId)).thenReturn(requestor);
        when(requestRepository.findById(1)).thenReturn(Optional.empty());

        RequestedItemResponseDto expectedItem = new RequestedItemResponseDto(createdItem.getId(),
                createdItem.getName(),
                createdItem.getDescription(),
                createdItem.getAvailable(),
                request.getId());

        assertThrows(NoSuchElementException.class, () -> service.getSingleRequestById(1, requestorId));
    }

    @Test
    void getRequest_posititve() {
        Integer requestorId = 1;
        User requestor = new User(requestorId, "requestor", "requestor@mail.ru");
        LocalDateTime created = LocalDateTime.now().minusDays(1);

        Integer ownerId = 2;
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Request request = new Request(1,
                "desc1",
                requestor,
                created);

        when(requestRepository.findById(1)).thenReturn(Optional.of(request));
        Request actual = service.getRequest(1);
        assertEquals(actual, request);
    }
}