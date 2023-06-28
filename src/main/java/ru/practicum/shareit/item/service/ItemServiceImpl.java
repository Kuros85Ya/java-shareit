package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.comparator.BookingComparator;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comparator.ItemComparator;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemResponseDto getById(int id, int userId) {
        Item item = getItem(id);
        return enrichItemWithExtraData(item, userId);
    }

    @Override
    public Item create(Integer userId, Item item) {
        User owner = getOwner(userId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public CommentResponseDTO createComment(CommentDto comment, Integer userId, Integer itemId) {
        User author = getOwner(userId);
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

        User owner = getOwner(userId);

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
    public List<Item> search(String query) {
        if (query.isBlank()) {
            return Collections.emptyList();
        } else {
            return itemRepository.search(query);
        }
    }

    @Override
    public List<ItemResponseDto> getAllUserItems(Integer userId) {
        return itemRepository.findAll().stream()
                .filter(it -> it.getOwner().getId().equals(userId))
                .sorted(new ItemComparator())
                .map(it -> enrichItemWithExtraData(it, userId))
                .collect(Collectors.toList());
    }

    private User getOwner(Integer userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NoSuchElementException("Пользователь с ID = " + userId + " не найдена."));
    }

    private Item getItem(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new NoSuchElementException("Вещь с ID = " + itemId + " не найдена."));
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
}
