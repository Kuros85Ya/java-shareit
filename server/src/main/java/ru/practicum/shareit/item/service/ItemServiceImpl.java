package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.comparator.BookingComparator;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comparator.ItemComparator;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestServiceImpl requestService;

    @Override
    public ItemResponseDto getById(int id, int userId) {
        Item item = getItem(id);
        return enrichItemWithExtraData(item, userId);
    }

    @Override
    public CreatedItemResponseDto create(Integer userId, ItemRequestDto item) {
        User owner = userService.getUser(userId);
        Request request;
        if (item.getRequestId() != null) {
            request = requestService.getRequest(item.getRequestId());
        } else {
            request = null;
        }
        Item created = ItemMapper.toItem(item, owner, request);
        itemRepository.save(created);
        return ItemMapper.ioCreatedItemResponseDto(created);
    }

    @Override
    public CommentResponseDTO createComment(CommentDto comment, Integer userId, Integer itemId) {
        User author = userService.getUser(userId);
        Item item = getItem(itemId);
        checkIfAuthorWasOwner(author, item);
        Comment commentDb = commentRepository.save(CommentMapper.toComment(comment, author, item));
        return CommentMapper.toCommentResponseDTO(commentDb, author);
    }

    /**
     * Изменить можно название, описание и статус доступа к аренде. Редактировать вещь может только её владелец.
     **/
    @Override
    public Item update(Integer userId, ItemDto item) {
        Item oldItem = getItem(item.getId());
        checkIfUserIsOwner(userId, oldItem);

        User owner = userService.getUser(userId);

        String name;
        String description;
        Boolean available;

        if (item.getName() != null) {
            name = item.getName();
        } else {
            name = oldItem.getName();
        }

        if (item.getDescription() != null) {
            description = item.getDescription();
        } else {
            description = oldItem.getDescription();
        }

        if (item.getAvailable() != null) {
            available = item.getAvailable();
        } else {
            available = oldItem.getAvailable();
        }

        return itemRepository.save(new Item(item.getId(), name, description, available, owner, null));
    }

    @Override
    public List<Item> search(String query, Integer from, Integer size) {
        PageRequest request = RequestMapper.toPageRequest(from, size);

        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(query, request);
        }
    }

    @Override
    public List<ItemResponseDto> getAllUserItems(Integer userId, Integer from, Integer size) {
        PageRequest request = RequestMapper.toPageRequest(from, size);

        return itemRepository.findAll(request).stream()
                .filter(it -> it.getOwner().getId().equals(userId))
                .sorted(new ItemComparator())
                .map(it -> enrichItemWithExtraData(it, userId))
                .collect(Collectors.toList());
    }

    private void checkIfUserIsOwner(Integer userId, Item item) {
        Integer ownerId = item.getOwner().getId();

        if (!ownerId.equals(userId))
            throw new NoSuchElementException("Пользователь id " + userId + " не является собственником вещи " + item.getId());
    }

    private void checkIfAuthorWasOwner(User user, Item item) {
        List<Booking> booking = bookingRepository.getBookingsByBookerAndItemAndStatusEqualsAndStartBefore(user, item, Status.APPROVED, LocalDateTime.now());
        if (booking == null || booking.isEmpty()) {
            throw new ValidationException("Пользователь не пользовался этой вещью");
        }
    }

    private ItemResponseDto enrichItemWithExtraData(Item item, Integer userId) {
        List<Booking> bookings = bookingRepository.findAllByItem(item);
        Booking lastBooking = bookings.stream()
                .filter(it -> it.getStatus().equals(Status.WAITING) || it.getStatus().equals(Status.APPROVED))
                .filter(it -> it.getStart().isBefore(LocalDateTime.now()))
                .min(new BookingComparator())
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(it -> it.getStatus().equals(Status.WAITING) || it.getStatus().equals(Status.APPROVED))
                .filter(it -> it.getStart().isAfter(LocalDateTime.now()))
                .max(new BookingComparator())
                .orElse(null);

        List<CommentResponseDTO> comments = commentRepository.findCommentByItemEquals(item).stream().map(it -> CommentMapper.toCommentResponseDTO(it, it.getAuthor())).collect(Collectors.toList());
        if (item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemResponseDto(item, nextBooking, lastBooking, comments);
        } else {
            return ItemMapper.toItemResponseDto(item, null, null, comments);
        }
    }

    public Item getItem(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new NoSuchElementException("Вещь с ID = " + itemId + " не найдена."));
    }
}
