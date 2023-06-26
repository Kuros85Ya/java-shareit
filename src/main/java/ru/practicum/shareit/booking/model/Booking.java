package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**Класс Booking содержит следующие поля:
 id — уникальный идентификатор бронирования;
 start — дата и время начала бронирования;
 end — дата и время конца бронирования;
 item — вещь, которую пользователь бронирует;
 booker — пользователь, который осуществляет бронирование;
 status — статус бронирования. Может принимать одно из следующих значений:
 WAITING — новое бронирование, ожидает одобрения, APPROVED — бронирование
 подтверждено владельцем, REJECTED — бронирование отклонено владельцем,
 CANCELED — бронирование отменено создателем.**/
@Data
@Entity
@Table(name = "bookings", schema = "public")
@RequiredArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @Column(name = "start_date")
    private LocalDateTime start;
    @NotNull
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne
    private Item item;
    @ManyToOne
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}
