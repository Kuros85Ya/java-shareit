package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.OWNER_ID_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(OWNER_ID_HEADER) long userId,
                                         @RequestBody @Valid RequestRequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUgetResponsesToUserRequestserBookings(@RequestHeader(OWNER_ID_HEADER) long userId) {
        log.info("Get all items created by requests of user, userId={}", userId);
        return requestClient.getItemsThatWereCreatedByRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getBooking(@RequestHeader(OWNER_ID_HEADER) long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all requests except created by user, userId={}", userId);
        return requestClient.getAllRequestsPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getSingleRequestItems(@RequestHeader(OWNER_ID_HEADER) long userId, @PathVariable long requestId) {
        log.info("Get items created by request requestId={} of user, userId={}", requestId, userId);
        return requestClient.getSingleRequestById(userId, requestId);
    }
}
