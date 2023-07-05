package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService service;

    @SneakyThrows
    @Test
    void create_whenAllDataPresent_thenCreated() {
        Integer bookerId = 1;
        Integer ownerId = 1;
        Integer itemId = 1;
        LocalDateTime startDt = LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(itemId, "itemName", "itemDesc", true, owner, null);

        BookingDto bookingDto = new BookingDto(itemId, startDt, endDt);
        Booking expected = BookingMapper.toBooking(bookingDto, booker, existingItem, Status.WAITING);

        when(service.create(bookerId, bookingDto)).thenReturn(expected);

        String actual = mockMvc.perform(post("/bookings")
                        .header(OWNER_ID_HEADER, bookerId)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(service).create(bookerId, bookingDto);
    }

    @SneakyThrows
    @Test
    void update_whenAllDataPresent_thenUpdated() {
        Integer bookingId = 1;
        Integer ownerId = 1;
        Integer bookerId = 2;
        Boolean accepted = true;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        LocalDateTime startDt =  LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(bookingId, startDt, endDt, existingItem, booker, Status.APPROVED, created);

        when(service.setAcceptStatus(ownerId, bookingId, accepted)).thenReturn(booking);

        String actual = mockMvc.perform(patch("/bookings/" + bookingId + "?approved=true")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), actual);
        verify(service).setAcceptStatus(ownerId, bookingId, accepted);
    }

    @SneakyThrows
    @Test
    void getBooking_whenBookingIsPresent_thenSingleBookingReturned() {
        Integer bookingId = 1;
        Integer ownerId = 1;
        Integer bookerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        LocalDateTime startDt =  LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(bookingId, startDt, endDt, existingItem, booker, Status.APPROVED, created);

        when(service.getBooking(ownerId, bookingId)).thenReturn(booking);

        String actual = mockMvc.perform(get("/bookings/" + bookingId)
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), actual);
        verify(service).getBooking(ownerId, bookingId);
    }

    @SneakyThrows
    @Test
    void getUserBookings_whenBookingsExist_thenAllAreReturned() {
        Integer bookingId = 1;
        Integer ownerId = 1;
        Integer bookerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        LocalDateTime startDt =  LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(bookingId, startDt, endDt, existingItem, booker, Status.APPROVED, created);

        when(service.getAllUserBookings(ownerId, State.ALL, 0, 10)).thenReturn(List.of(booking));

        String actual = mockMvc.perform(get("/bookings?state=ALL")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(booking)), actual);
        verify(service).getAllUserBookings(ownerId, State.ALL, 0, 10);
    }

    @SneakyThrows
    @Test
    void getAllUserItemBookings_whenUserHsBookingsOnItem_thenHisBookingsReturned() {
        Integer bookingId = 1;
        Integer ownerId = 1;
        Integer bookerId = 2;

        User booker = new User(bookerId, "test", "test@mail.ru");
        User owner = new User(ownerId, "owner", "owner@mail.ru");
        Item existingItem = new Item(1, "itemName", "itemDesc", true, owner, null);
        LocalDateTime startDt =  LocalDateTime.now().plusHours(1);
        LocalDateTime endDt = LocalDateTime.now().plusDays(10);
        LocalDateTime created = LocalDateTime.now();

        Booking booking = new Booking(bookingId, startDt, endDt, existingItem, booker, Status.APPROVED, created);

        when(service.getAllUserItemBookings(ownerId, State.ALL, 0, 10)).thenReturn(List.of(booking));

        String actual = mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header(OWNER_ID_HEADER, ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(booking)), actual);
        verify(service).getAllUserItemBookings(ownerId, State.ALL, 0, 10);
    }
}