package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 *  id  — уникальный идентификатор бронирования;
 *  start  — дата и время начала бронирования;
 *  end — дата и время конца бронирования;
 *  item — вещь, которую пользователь бронирует;
 *  booker — пользователь, который осуществляет бронирование;
 *   status — статус бронирования. Может принимать одно из следующих**/
@Data
public class Booking {
    private final Integer id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final User booker;
    private final Status status;
}
