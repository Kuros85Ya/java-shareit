package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, String> urlParams = Map.of("state", state.name(),
                "from", from.toString(),
                "size", size.toString());

        String path = UriComponentsBuilder.fromUriString("/owner?state={state}&from={from}&size={size}")
                .buildAndExpand(urlParams)
                .toUriString();

        return get(path, userId);
    }

    public ResponseEntity<Object> getAllUserItemBookings(long userId, BookingState state, Integer from, Integer size) {
        Map<String, String> urlParams = Map.of("state", state.name(),
                "from", from.toString(),
                "size", size.toString());

        String path = UriComponentsBuilder
                .fromUriString("/owner?state={state}&from={from}&size={size}")
                .buildAndExpand(urlParams)
                .toUriString();

        return get(path, userId);
    }


    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> setAcceptStatus(long userId, long bookingId, Boolean accepted) {
        Map<String, String> urlParams = Map.of("bookingId", String.valueOf(bookingId));

        String path = UriComponentsBuilder
                .fromUriString("/{bookingId}")
                .queryParam("approved", accepted)
                .buildAndExpand(urlParams)
                .toUriString();

        return patch(path, userId);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

}
