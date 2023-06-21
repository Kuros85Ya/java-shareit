package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody @Valid Item item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Создаем вещь: {}", item);
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item update(@PathVariable Integer itemId, @RequestBody @Valid ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Изменяем вещь: {}", item);
        item.setId(itemId);
        return itemService.update(userId, item);
    }

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable int itemId) {
        log.info("Вывести вещь ID = {}", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping()
    public List<Item> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Вывести вещи пользователя ID = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public Set<Item> getUserItems(@RequestParam(name = "text", defaultValue = "") String query) {
        log.info("Вывести вещи по запросу {}", query);
        return itemService.search(query);
    }
}
