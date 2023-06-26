package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponseDto getById(int itemId, int userId);

    Item create(Integer userId, Item item);

    CommentResponseDTO createComment(CommentDto comment, Integer userId, Integer authorId);

    Item update(Integer userId, ItemDto item);

    List<Item> search(String query);

    List<ItemResponseDto> getAllUserItems(Integer userId);
}
