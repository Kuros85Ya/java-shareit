package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    UserServiceImpl userService;
    @Mock
    ItemServiceImpl itemService;
    @Mock
    BookingRepository repository;

    @InjectMocks
    BookingServiceImpl service;

    @Test
    void create_whenAllFieldsValid_thenBookingCreated() {
        Integer userId = 1;
        LocalDateTime startDt = LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        User booker = new User(1, "test", "test@mail.ru");
        User owner = new User(2, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);

        BookingDto bookingDto = new BookingDto(1, startDt, endDt);

        when(userService.getUser(userId)).thenReturn(booker);
        when(itemService.getItem(bookingDto.getItemId())).thenReturn(existingItem);

        Booking createdBooking = BookingMapper.toBooking(bookingDto, booker, existingItem, Status.WAITING);

        when(repository.save(any())).thenReturn(createdBooking);

        Booking actual = service.create(userId, bookingDto);
        assertEquals(actual, createdBooking);
    }

    @Test
    void getBooking_whenOwnerAccepts_thenStatusChangesToAccepted() {
        User booker = new User(1, "test", "test@mail.ru");
        User owner = new User(2, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        LocalDateTime startDt =  LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(1, startDt, endDt, existingItem, booker, Status.WAITING, created);
        when(repository.findById(1)).thenReturn(Optional.of(booking));
        Booking actual = service.getBooking(1, 1);
        assertEquals(actual, booking);
    }

    @Test
    void getBooking_whenNonExistedBookingGet_ThenExceptionIsThrown() {
        assertThrows(NoSuchElementException.class, () -> service.getBooking(1, 1));
    }


    @Test
    void getBooking_whenNotOwnerTriesToGet_ThenExceptionIsThrown() {
        User booker = new User(1, "test", "test@mail.ru");
        User owner = new User(2, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        LocalDateTime startDt = LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(1, startDt, endDt, existingItem, owner, Status.WAITING, created);
        when(repository.findById(1)).thenReturn(Optional.of(booking));
        assertThrows(NoSuchElementException.class, () -> service.getBooking(3, 1));
    }

    @Test
    void create_whenTryingBookMyOwnItem_thenNoSuchElementExceptionIsThrown() {
        Integer userId = 1;
        User booker = new User(1, "test", "test@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);

        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(10));

        when(userService.getUser(userId)).thenReturn(booker);
        when(itemService.getItem(bookingDto.getItemId())).thenReturn(existingItem);

        assertThrows(NoSuchElementException.class, () -> service.create(userId, bookingDto));
    }

    @Test
    void setAcceptStatus_whenBookingIsApproved_ThenStatusChngesToPositive() {
        Integer bookerId = 1;
        Integer ownerId = 2;
        Integer bookingId = 1;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);

        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Booking booking = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);
        Booking bookingAccepted = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.APPROVED, created);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(repository.save(booking)).thenReturn(bookingAccepted);
        Booking finalBooking = service.setAcceptStatus(ownerId, bookingId, true);
        assertEquals(finalBooking.getStatus(), Status.APPROVED);
    }

    @Test
    void setAcceptStatus_whenBookingIsDeclined_thenStatusChangesToRejected() {
        Integer bookerId = 1;
        Integer ownerId = 2;
        Integer bookingId = 1;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);

        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Booking booking = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);
        Booking bookingDeclined = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.REJECTED, created);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));

        when(repository.save(booking)).thenReturn(bookingDeclined);
        Booking finalBooking = service.setAcceptStatus(ownerId, bookingId, false);
        assertEquals(finalBooking.getStatus(), Status.REJECTED);
    }

    @Test
    void setAcceptStatus_whenStatusTriesToChangeNotOwner_thenNoSuchElementExceptionIsThrown() {
        Integer bookerId = 1;
        Integer ownerId = 2;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);

        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Booking booking = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(NoSuchElementException.class, () -> service.setAcceptStatus(ownerId, bookingId, true));
    }

    @Test
    void setAcceptStatus_whenStatusIsNotWaiting_thenValidationExceptionIsThrown() {
        Integer bookerId = 1;
        Integer ownerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);

        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Booking booking = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.CANCELED, created);
        when(repository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> service.setAcceptStatus(ownerId, bookingId, true));
    }

    @Test
    void getAllUserBookings_All_whenBookingsArePresent_thenAllAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);

        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.findAllByBookerOrderByCreatedDesc(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.ALL, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserBookings_Past_whenBookingsWereInPast_thenTheyAreReturned() {
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getBookingsByBookerAndEndIsBefore(any(), any())).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.PAST, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserBookings_Current_whenBookingsAreCurrentlyActive_thenTheyAreReturned() {
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getBookingsByBookerAndStartIsBeforeAndEndIsAfter(any(), any(), any())).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.CURRENT, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserBookings_Future_whenBookingsAreInFuture_thenTheyAreReturned() {
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getBookingsByBookerAndStartIsAfter(any(), any())).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.FUTURE, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserBookings_Waiting_whenBookingsAreWaiting_thenTheyAreReturned() {
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getBookingsByBookerAndStatusEquals(booker, Status.WAITING)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.WAITING, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserBookings_Rejected_whenBookingsAreRejected_thenTheyAreReturned() {
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getBookingsByBookerAndStatusEquals(booker, Status.REJECTED)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserBookings(bookerId, State.REJECTED, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_All_WhenAsked_thenAllAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.ALL, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_Past_WhenWereInPast_thenAllUserItemsAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2019, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2020, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.PAST, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_Current_WhenBookingsAreCurrentlyActive_thenTheyAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2019, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2030, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.CURRENT, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_Future_WhenBookingsAreInFuture_thenTheyAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2040, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2050, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.FUTURE, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_Waiting_whenBookingsAreWaiting_thenTheyAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2040, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2050, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.WAITING, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.WAITING, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void getAllUserItemsBookings_Rejected_WhenBookingsAreRejected_thenTheyAreReturned() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Integer bookerId = 1;
        Integer bookingId = 1;
        User booker = new User(bookerId, "test", "test@mail.ru");
        LocalDateTime startTime = LocalDateTime.of(2040, Month.JANUARY, 10, 10, 10);
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.of(2050, Month.JANUARY, 10, 10, 10);

        Item existingItem = new Item(1, "itemName", "itemDesc", true, booker, null);
        Booking booking1 = new Booking(bookingId, startTime, endTime, existingItem, booker, Status.REJECTED, created);

        List<Booking> bookingList = Arrays.asList(booking1); //mutable list
        when(userService.getUser(bookerId)).thenReturn(booker);
        when(repository.getAllUserItems(booker, pageRequest)).thenReturn(bookingList);
        List<Booking> actual = service.getAllUserItemBookings(bookerId, State.REJECTED, 0, 10);
        assertEquals(actual, bookingList);
    }

    @Test
    void testGetBooking() {
    }
}