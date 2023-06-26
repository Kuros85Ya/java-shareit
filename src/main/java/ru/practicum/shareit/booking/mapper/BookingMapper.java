package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, User booker, Item item, Status status) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                status);
    }
}
