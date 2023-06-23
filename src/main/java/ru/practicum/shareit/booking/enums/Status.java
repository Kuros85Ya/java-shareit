package ru.practicum.shareit.booking.enums;

public enum Status {
    WAITING, // — новое бронирование, ожидает одобрения,
    APPROVED, // бронирование подтверждено владельцем
    REJECTED, // — бронирование
    CANCELED // — бронирование отменено создателем.
}
