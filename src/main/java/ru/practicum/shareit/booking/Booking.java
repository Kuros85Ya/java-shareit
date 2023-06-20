package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.User;

import java.time.LocalDateTime;

@Data
public class Booking {
    private final Integer id; // — уникальный идентификатор бронирования;
    private final LocalDateTime start; // — дата и время начала бронирования;
    private final LocalDateTime end; // — дата и время конца бронирования;
    private final Item item; // — вещь, которую пользователь бронирует;
    private final User booker; // — пользователь, который осуществляет бронирование;
    private final Status status; // — статус бронирования. Может принимать одно из следующих
}
