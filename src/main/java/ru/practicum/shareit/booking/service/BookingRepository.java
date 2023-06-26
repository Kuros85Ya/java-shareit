package ru.practicum.shareit.booking.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBooker(User booker);

    List<Booking> getBookingsByBookerAndStartIsAfter(User booker, LocalDateTime dateAfter);

    List<Booking> getBookingsByBookerAndEndIsBefore(User booker, LocalDateTime dateBefore);

    List<Booking> getBookingsByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime dateAfter, LocalDateTime dateBefore);

    List<Booking> getBookingsByBookerAndStatusEquals(User booker, Status status);

    List<Booking> getBookingsByBookerAndItemAndStatusEqualsAndStartBefore(User booker, Item item, Status status, LocalDateTime timeBefore);

    List<Booking> findAllByItem(Item item);
}
