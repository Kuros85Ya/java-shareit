package ru.practicum.shareit.booking.comparator;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Comparator;

public class BookingComparator implements Comparator<Booking> {
    @Override
    public int compare(Booking booking1, Booking booking2) {
        if (booking1.getStart().equals(booking2.getStart())) {
            return booking1.getId().compareTo(booking2.getId());
        } else
            return booking2.getStart().compareTo(booking1.getStart());
    }
}
