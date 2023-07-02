package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.dto.RequestedItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserServiceImpl userService;

    @Override
    public Request create(Integer userId, RequestRequestDTO request) {
        Request req = RequestMapper.toRequest(request, userService.getUser(userId));
        return requestRepository.save(req);
    }

    @Override
    public List<RequestResponseDTO> getItemsThatWereCreatedByRequest(Integer userId) {
        userService.getUser(userId);
        List<Request> allRequests = requestRepository.findAll();
        List<Item> allItems = allRequests.stream().map(itemRepository::findAllByRequest).flatMap(List::stream).collect(Collectors.toList());

        List<RequestedItemResponseDto> itemResponses = allItems.stream().map(RequestMapper::toItemRequestResponseDto).collect(Collectors.toList());
        return RequestMapper.toRequestResponseDto(allRequests, itemResponses);
    }

    @Override
    public List<RequestResponseDTO> getAllRequestsPageable(Integer userId, Integer from, Integer size) {
        PageRequest request = RequestMapper.toPageRequest(from, size);
        List<Request> neededRequests = requestRepository.findAll(request)
                .stream()
                .filter(it -> !it.getRequestor().getId().equals(userId))
                .collect(Collectors.toList());
        List<RequestedItemResponseDto> allItems = neededRequests.stream().map(itemRepository::findAllByRequest).flatMap(List::stream).map(RequestMapper::toItemRequestResponseDto).collect(Collectors.toList());

        return RequestMapper.toRequestResponseDto(neededRequests, allItems);
    }

    @Override
    public RequestResponseDTO getSingleRequestById(Integer requestId, Integer userId) {
        userService.getUser(userId);
        Request request = getRequest(requestId);
        List<RequestedItemResponseDto> allItems = itemRepository.findAllByRequest(request).stream().map(RequestMapper::toItemRequestResponseDto).collect(Collectors.toList());
        return RequestMapper.toRequestResponseDto(List.of(request), allItems).get(0);
    }

    public Request getRequest(Integer requestId) {
        return requestRepository.findById(requestId).orElseThrow(()
                -> new NoSuchElementException("Запрос с ID = " + requestId + " не найден."));
    }
}
