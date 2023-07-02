package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.comparator.ItemComparator;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequestRepository requestRepository;

    @BeforeEach
    private void addData() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);

        User booker = userRepository.save(new User(null, "test", "test@mail.ru"));
        User owner = userRepository.save(new User(null, "owner", "owner@mail.ru"));

        Request request = requestRepository.save(new Request(1, "ReqDescription", booker, created));

        Item item1 = new Item(null, "itemName", "itemDesc", true, owner, request);
        Item item2 = new Item(null, "itemName2", "itemDesc2", true, booker, request);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @Test
    void search_whenSeveralItemsMatch_thenTheyAreReturned() {
        LocalDateTime created = LocalDateTime.of(2022, 10, 10, 10, 10);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Item> items = itemRepository.search("nAm", pageRequest);

        User booker = new User(1, "test", "test@mail.ru");
        User owner = new User(2, "owner", "owner@mail.ru");
        Request request = new Request(1, "ReqDescription", booker, created);

        Item item1 = new Item(1, "itemName", "itemDesc", true, owner, request);
        Item item2 = new Item(2, "itemName2", "itemDesc2", true, booker, request);

        List<Item> expected = Arrays.asList(item1, item2);
        items.sort(new ItemComparator());
        expected.sort(new ItemComparator());

        assertEquals(items, expected);
    }

    @AfterEach
    private void deleteData() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}