package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody @Valid Item item, @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Создаем вещь: {}", item);
        return itemService.create(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDTO createComment(@RequestBody @Valid CommentDto comment, @RequestHeader(OWNER_ID_HEADER) Integer userId, @PathVariable Integer itemId) {
        log.info("Оставлен комментарий к вещи: {}", itemId);
        return itemService.createComment(comment, userId, itemId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@PathVariable Integer itemId, @RequestBody @Valid ItemDto item, @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Изменяем вещь: {}", item);
        item.setId(itemId);
        return itemService.update(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getById(@PathVariable int itemId, @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Вывести вещь ID = {}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping()
    public List<ItemResponseDto> getUserItems(@RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Вывести вещи пользователя ID = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<Item> getUserItems(@RequestParam(name = "text", defaultValue = "") String query) {
        log.info("Вывести вещи по запросу {}", query);
        return itemService.search(query);
    }
}
