package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.comparator.BookingComparator;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getAllUserItems_whenSeveralItemsHaveBooking_ThenAllAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDateTime startTime1 = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime endTime1 = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);
        LocalDateTime created1 = LocalDateTime.of(2022, 10, 10, 10, 10);

        LocalDateTime startTime2 = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime endTime2 = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);
        LocalDateTime created2 = LocalDateTime.of(2022, 10, 10, 10, 10);

        User booker = userRepository.save(new User(null, "test", "test@mail.ru"));
        userRepository.save(new User(null, "owner", "owner@mail.ru"));

        Request request = requestRepository.save(new Request(1, "ReqDescription", booker, created1));

        Item item1 = new Item(null, "itemName", "itemDesc", true, booker, request);
        Item item2 = new Item(null, "itemName2", "itemDesc2", true, booker, request);
        itemRepository.save(item1);
        itemRepository.save(item2);

        Booking booking1 = bookingRepository.save(new Booking(null, startTime1, endTime1, item1, booker, Status.WAITING, created1));
        Booking booking2 = bookingRepository.save(new Booking(null, startTime2, endTime2, item2, booker, Status.WAITING, created2));

        List<Booking> bookingListExpected = Arrays.asList(booking1, booking2);
        List<Booking> bookingListActual = bookingRepository.getAllUserItems(booker, pageRequest);

        bookingListExpected.sort(new BookingComparator());
        bookingListActual.sort(new BookingComparator());
        assertEquals(bookingListActual, bookingListExpected);
    }

    @AfterEach
    void deleteData() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}