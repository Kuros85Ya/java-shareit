package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerOrderByCreatedDesc(User booker, Pageable pageable);

    List<Booking> getBookingsByBookerAndStartIsAfter(User booker, LocalDateTime dateAfter);

    List<Booking> getBookingsByBookerAndEndIsBefore(User booker, LocalDateTime dateBefore);

    List<Booking> getBookingsByBookerAndStartIsBeforeAndEndIsAfter(User booker, LocalDateTime dateAfter, LocalDateTime dateBefore);

    List<Booking> getBookingsByBookerAndStatusEquals(User booker, Status status);

    List<Booking> getBookingsByBookerAndItemAndStatusEqualsAndStartBefore(User booker, Item item, Status status, LocalDateTime timeBefore);

    List<Booking> findAllByItem(Item item);

    @Query("select b from Item i join Booking b on b.item = i where i.available = true and i.owner = ?1 order by b.created desc")
    List<Booking> getAllUserItems(User user, Pageable pageable);
}
