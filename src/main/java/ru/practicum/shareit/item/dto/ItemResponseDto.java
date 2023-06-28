package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private Integer id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentResponseDTO> comments;
}
