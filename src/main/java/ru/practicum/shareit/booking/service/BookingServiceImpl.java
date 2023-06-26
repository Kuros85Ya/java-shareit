package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingComparator;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserRepository;

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
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;


    @Override
    public Booking create(Integer userId, BookingDto bookingDto) throws ValidationException {
        User booker = getUser(userId);
        Item item = getItem(bookingDto.getItemId());
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        validateBooking(newBooking);
        return bookingRepository.save(newBooking);
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
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllUserBookings(Integer userId, State state) {
        List<Booking> bookings;
        User user = getUser(userId);

        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker(user);
                break;
            case PAST:
                bookings = bookingRepository.getBookingsByBookerAndEndIsBefore(user, LocalDateTime.now())
                        .stream()
                        .filter(it -> (it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING)))
                        .collect(Collectors.toList());
                break;
            case CURRENT:
                bookings = bookingRepository.getBookingsByBookerAndStartIsBeforeAndEndIsAfter(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.getBookingsByBookerAndStartIsAfter(user, LocalDateTime.now())
                        .stream().filter(it -> (it.getStatus().equals(Status.APPROVED) || it.getStatus().equals(Status.WAITING)))
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookings = bookingRepository.getBookingsByBookerAndStatusEquals(user, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.getBookingsByBookerAndStatusEquals(user, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Такого статуса не существует");
        }
        bookings.sort(new BookingComparator());
        return bookings;
    }

    @Override
    public List<Booking> getAllUserItemBookings(Integer userId, State state) {
        User user = getUser(userId);
        List<Booking> bookings;

        List<Item> itemsOwned = itemRepository.findAllByOwner(user);
        List<Booking> allUserBookings = itemsOwned.stream().map(bookingRepository::findAllByItem).flatMap(List::stream).collect(Collectors.toList());

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

    private Booking getBooking(Integer bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(()
                -> new NoSuchElementException("Бронирование с ID = " + bookingId + " не найдено."));
    }

    private User getUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NoSuchElementException("Пользователь с ID = " + userId + " не найден."));
    }

    private Item getItem(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new NoSuchElementException("Вещь с ID = " + itemId + " не найдена."));
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
