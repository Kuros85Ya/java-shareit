package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findRequestsWithItemsByUser_whenSeveralItemsOnOneRequest_thenAllItemsAreReturned() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);

        User requestor = userRepository.save(new User(null, "test", "test@mail.ru"));
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));

        Request request = requestRepository.save(new Request(1, "ReqDescription", requestor, created));

        Item item1 = itemRepository.save(new Item(null, "itemName", "itemDesc", true, owner, request));
        Item item2 = itemRepository.save(new Item(null, "itemName2", "itemDesc2", true, owner, request));

        List<Object[]> objects = requestRepository.findRequestsWithItemsByUser(requestor);

        List<Item> items = new ArrayList<>();
        List<Request> requests = new ArrayList<>();

        for (Object[] o : objects) {
            Item item = (Item) o[0];
            if (item != null) {
                items.add(item);
            }
            Request req = (Request) o[1];
            requests.add(req);
        }

        assertEquals(items, List.of(item1, item2));
        assertEquals(requests, List.of(request, request));

    }

    @Test
    void findAllRequestsOfOtherUsersWithItemsPageable_whenRequestsAreByUser_thenTheyAreFilteredAndNotReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);

        User requestor = userRepository.save(new User(null, "test", "test@mail.ru"));
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));

        Request request = requestRepository.save(new Request(1, "ReqDescription", requestor, created));

        itemRepository.save(new Item(null, "itemName", "itemDesc", true, owner, request));
        itemRepository.save(new Item(null, "itemName2", "itemDesc2", true, owner, request));

        List<Object[]> objects = requestRepository.findAllRequestsOfOtherUsersWithItemsPageable(requestor, pageRequest);

        List<Item> items = new ArrayList<>();
        List<Request> requests = new ArrayList<>();

        for (Object[] o : objects) {
            Item item = (Item) o[0];
            if (item != null) {
                items.add(item);
            }
            Request req = (Request) o[1];
            requests.add(req);
        }

        assertEquals(items, Collections.emptyList());
        assertEquals(requests, Collections.emptyList());
    }

    @Test
    void findAllRequestsOfOtherUsersWithItemsPageable_whenRequestsAreByOtherPeople_thenTheyAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);

        User requestor = userRepository.save(new User(null, "test", "test@mail.ru"));
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));

        Request request = requestRepository.save(new Request(1, "ReqDescription", requestor, created));

        Item item1 = itemRepository.save(new Item(null, "itemName", "itemDesc", true, owner, request));
        Item item2 = itemRepository.save(new Item(null, "itemName2", "itemDesc2", true, owner, request));

        List<Object[]> objects = requestRepository.findAllRequestsOfOtherUsersWithItemsPageable(owner, pageRequest);

        List<Item> items = new ArrayList<>();
        List<Request> requests = new ArrayList<>();

        for (Object[] o : objects) {
            Item item = (Item) o[0];
            if (item != null) {
                items.add(item);
            }
            Request req = (Request) o[1];
            requests.add(req);
        }

        assertEquals(items, List.of(item1, item2));
        assertEquals(requests, List.of(request, request));
    }

    @AfterEach
    private void deleteData() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}