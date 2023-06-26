package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    /**Запрос может быть создан любым пользователем, а затем подтверждён владельцем вещи.
     * Эндпоинт — POST /bookings. После создания запрос находится в статусе WAITING — «ожидает подтверждения».**/
    Booking create(Integer userId, BookingDto booking);

    Booking setAcceptStatus(Integer userId, Integer bookingId, Boolean accepted);

    /**Получение данных о конкретном бронировании (включая его статус).
     * Может быть выполнено либо автором бронирования, либо владельцем вещи, к которой относится бронирование. **/
    Booking getBooking(Integer userId, Integer bookingId);

    /**Получение списка всех бронирований текущего пользователя.
     * Параметр state необязательный и по умолчанию равен ALL (англ. «все»).
     * Бронирования должны возвращаться отсортированными по дате от более новых к более старым.**/
    List<Booking> getAllUserBookings(Integer userId, State state);

    /**Получение списка бронирований для всех вещей текущего пользователя.
     * Этот запрос имеет смысл для владельца хотя бы одной вещи. Работа параметра state аналогична его работе в предыдущем сценарии.**/
    List<Booking> getAllUserItemBookings(Integer userId, State state);
}
