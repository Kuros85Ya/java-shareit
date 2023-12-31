package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public class ItemMapper {

    public static Item toItem(ItemRequestDto itemRequestDto, User owner, Request request) {
        return new Item(null,
                itemRequestDto.getName(),
                itemRequestDto.getDescription(),
                itemRequestDto.getAvailable(),
                owner,
                request);
    }

    public static CreatedItemResponseDto ioCreatedItemResponseDto(Item item) {
        Integer requestId;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        } else requestId = null;

        return new CreatedItemResponseDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId);
    }

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
