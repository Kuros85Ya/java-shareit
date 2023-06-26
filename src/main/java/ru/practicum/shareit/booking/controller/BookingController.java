package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@RequestBody @Valid BookingDto booking, @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Создаем вещь: {}", booking);
        return service.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking update(@PathVariable Integer bookingId,
                       @RequestParam(name = "approved") Boolean accepted,
                       @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Меняется статус по бронированию: " + bookingId);
        return service.setAcceptStatus(userId, bookingId, accepted);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@PathVariable int bookingId,
                              @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Запрошены данные по бронированию " + bookingId);
        return service.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<Booking> getUserBookings(@RequestHeader(OWNER_ID_HEADER) Integer userId, @RequestParam(name = "state", defaultValue = "ALL") State state) {
        log.info("Вывести бронирование пользователя " + userId);
        return service.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllUserItemBookings(@RequestHeader(OWNER_ID_HEADER) Integer userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") State state) {
        log.info("Запрос всех бронирований по всем вещам пользователя " + userId);
        return service.getAllUserItemBookings(userId, state);
    }
}
