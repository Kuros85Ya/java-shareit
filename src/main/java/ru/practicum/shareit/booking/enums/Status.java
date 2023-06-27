package ru.practicum.shareit.booking.enums;

/**
 * WAITING, — новое бронирование, ожидает одобрения, значение при создании бронирования
 * APPROVED - бронирование подтверждено владельцем
 * REJECTED — бронирование отклонено владельцем
 * CANCELED — бронирование отменено создателем.
 **/
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
