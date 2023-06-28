package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public class ItemMapper {

    public static ItemResponseDto toItemResponseDto(Item item, Booking nextBooking, Booking lastBooking, List<CommentResponseDTO> comments) {
        BookingInfoDto lastBook;
        BookingInfoDto nextBook;

        if (lastBooking == null) {
            lastBook = null;
        } else {
            lastBook = new BookingInfoDto(lastBooking.getId(), lastBooking.getBooker().getId());
        }

        if (nextBooking == null) {
            nextBook = null;
        } else {
            nextBook = new BookingInfoDto(nextBooking.getId(), nextBooking.getBooker().getId());
        }

        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                null,
                lastBook,
                nextBook,
                comments);
    }
}
