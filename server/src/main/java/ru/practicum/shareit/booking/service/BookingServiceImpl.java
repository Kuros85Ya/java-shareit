package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.comparator.BookingComparator;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;
    private final BookingRepository repository;


    @Override
    public Booking create(Integer userId, BookingDto bookingDto) throws ValidationException {
        User booker = userService.getUser(userId);
        Item item = itemService.getItem(bookingDto.getItemId());
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        validateBooking(newBooking);
        return repository.save(newBooking);
    }

    @Override
    public Booking getBooking(Integer userId, Integer bookingId) {
        Booking booking = getBooking(bookingId);
        checkIfUserCanGetBookingInfo(userId, booking);
        return booking;
    }

    @Override
    public Booking setAcceptStatus(Integer userId, Integer bookingId, Boolean accepted) {
        Booking booking = getBooking(bookingId);
        checkIfUserCanChangeBookingStatus(userId, booking);
        checkIfStatusCanBeChanged(booking);
        if (accepted) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return repository.save(booking);
    }

    @Override
    public List<Booking> getAllUserBookings(Integer userId, State state, Integer from, Integer size) {
        List<Booking> bookings;
        User user = userService.getUser(userId);
        PageRequest request = RequestMapper.toPageRequest(from, size);

        switch (state) {
            case ALL:
                bookings = repository.findAllByBookerOrderByCreatedDesc(user, request);
                break;
            case PAST:
                bookings = repository.getBookingsByBookerAndEndIsBefore(user, LocalDateTime.now())
                        .stream()
                        .filter(it -> (it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING)))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = repository.getBookingsByBookerAndStartIsBeforeAndEndIsAfter(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                bookings = repository.getBookingsByBookerAndStartIsAfter(user, LocalDateTime.now())
                        .stream().filter(it -> (it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING)))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = repository.getBookingsByBookerAndStatusEquals(user, Status.WAITING);
                break;
            case REJECTED:
                bookings = repository.getBookingsByBookerAndStatusEquals(user, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Такого статуса не существует");
        }
        bookings.sort(new BookingComparator());
        return bookings;
    }

    @Override
    public List<Booking> getAllUserItemBookings(Integer userId, State state, Integer from, Integer size) {
        User user = userService.getUser(userId);
        List<Booking> bookings;

        PageRequest request = RequestMapper.toPageRequest(from, size);

        List<Booking> allUserBookings = repository.getAllUserItems(user, request);

        switch (state) {
            case ALL:
                bookings = allUserBookings;
                break;
            case PAST:
                bookings = allUserBookings
                        .stream()
                        .filter(it -> it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING))
                        .filter(it -> it.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = allUserBookings
                        .stream()
                        .filter(it -> it.getStart().isBefore(LocalDateTime.now()))
                        .filter(it -> it.getEnd().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookings = allUserBookings
                        .stream()
                        .filter(it -> it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING))
                        .filter(it -> it.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = allUserBookings
                        .stream()
                        .filter(it -> it.getStatus().equals(Status.WAITING))
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookings = allUserBookings
                        .stream()
                        .filter(it -> it.getStatus().equals(Status.REJECTED))
                        .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Такого статуса не существует");
        }
        bookings.sort(new BookingComparator());
        return bookings;
    }

    private Boolean checkIfUserIsAuthor(Integer userId, Booking booking) {
        Integer ownerId = booking.getBooker().getId();
        return ownerId.equals(userId);
    }

    private Boolean checkIfUserIsOwner(Integer userId, Item item) {
        Integer ownerId = item.getOwner().getId();
        return ownerId.equals(userId);
    }

    private void checkIfUserCanGetBookingInfo(Integer userId, Booking booking) {
        if (!(checkIfUserIsOwner(userId, booking.getItem()) || checkIfUserIsAuthor(userId, booking))) {
            throw new NoSuchElementException("Пользователю  " + userId + " запрещен доступ к просмотру вещи " + booking.getId());
        }
    }

    private void checkIfUserCanChangeBookingStatus(Integer userId, Booking booking) {
        Item item = booking.getItem();

        if (!(item.getOwner().getId().equals(userId))) {
            throw new NoSuchElementException("Пользователю  " + userId + " запрещено менясть статус вещи " + booking.getId());
        }
    }

    private void checkIfStatusCanBeChanged(Booking booking) {
        if (booking.getStatus() != Status.WAITING) {
            throw new ValidationException("Статус бронирования нельзя изменить");
        }
    }

    public Booking getBooking(Integer bookingId) {
        return repository.findById(bookingId).orElseThrow(()
                -> new NoSuchElementException("Бронирование с ID = " + bookingId + " не найдено."));
    }

    private void validateBooking(Booking booking) {
        if (booking.getStart() == null || booking.getEnd() == null ||
                (booking.getEnd().isBefore(LocalDateTime.now()))
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().equals(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректная дата бронирования");
        }

        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NoSuchElementException("Пользователь не может взять в аренду свою вещь");
        }
    }
}
