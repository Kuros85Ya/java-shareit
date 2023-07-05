package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestHeader(OWNER_ID_HEADER) long userId, @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Creating item={}", requestDto);
        return itemClient.createItem(userId, requestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(OWNER_ID_HEADER) long userId,
                                                @PathVariable long itemId,
                                                @RequestBody @Valid CommentRequestDto requestDto) {
        log.info("Creating comment to item={} Comment={}", itemId, requestDto);
        return itemClient.createComment(requestDto, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(OWNER_ID_HEADER) long userId, @RequestBody @Valid ItemUpdateRequestDto requestDto, @PathVariable long itemId) {
        log.info("Changing item, itemId={}, updated Item={}", itemId, requestDto);
        return itemClient.updateItem(userId, itemId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItems(@RequestHeader(OWNER_ID_HEADER) Integer userId, @PathVariable long itemId) {
        log.info("Get item, itemId={}", userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(OWNER_ID_HEADER) long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all items of user={}, page from={}, size={}", userId, from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam(name = "text", defaultValue = "") String query, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search, query={}, page from={}, size={}", query, from, size);
        return itemClient.search(query, from, size);
    }
}
