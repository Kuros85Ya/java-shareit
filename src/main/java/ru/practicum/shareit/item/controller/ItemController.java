package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody @Valid Item item) {
        log.info("Создаем вещь: {}", item);
        return itemService.create(item);
    }

    @PostMapping("/{itemId}")
    public Item update(@RequestBody @Valid Item item) {
        log.info("Создаем пользователя: {}", item);
        return itemService.update(item);
    }

    @GetMapping
    public List<Item> getAll() {
        log.info("Вывести всех пользователей");
        return itemService.getAll();
    }

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable int itemId) {
        log.info("Вывести пользователя ID = {}", itemId);
        return itemService.getById(itemId);
    }

    @DeleteMapping("/{itemId}")
    public void remove(@PathVariable int itemId) {
        itemService.remove(itemId);
    }
}
