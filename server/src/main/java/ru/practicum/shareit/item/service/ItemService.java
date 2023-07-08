package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemResponseDto getById(int itemId, int userId);

    CreatedItemResponseDto create(Integer userId, ItemRequestDto item);

    CommentResponseDTO createComment(CommentDto comment, Integer userId, Integer authorId);

    Item update(Integer userId, ItemDto item);

    List<Item> search(String query, Integer from, Integer size);

    List<ItemResponseDto> getAllUserItems(Integer userId, Integer from, Integer size);
}
