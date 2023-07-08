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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
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
        User requestor = userService.getUser(userId);

        List<Item> items = new ArrayList<>();
        List<Request> requests = new ArrayList<>();
        List<Object[]> objects = requestRepository.findRequestsWithItemsByUser(requestor);
        parseRepositoryRequestsWithItemsObjects(objects, items, requests);

        List<RequestedItemResponseDto> allItemsResponse = items.stream().map(RequestMapper::toItemRequestResponseDto).collect(Collectors.toList());
        return RequestMapper.toRequestResponseDto(requests, allItemsResponse);
    }

    @Override
    public List<RequestResponseDTO> getAllRequestsPageable(Integer userId, Integer from, Integer size) {
        PageRequest request = RequestMapper.toPageRequest(from, size);
        User requestor = userService.getUser(userId);

        List<Item> items = new ArrayList<>();
        List<Request> requests = new ArrayList<>();
        List<Object[]> objects = requestRepository.findAllRequestsOfOtherUsersWithItemsPageable(requestor, request);
        parseRepositoryRequestsWithItemsObjects(objects, items, requests);

        List<RequestedItemResponseDto> allItemsResponse = items.stream().map(RequestMapper::toItemRequestResponseDto).collect(Collectors.toList());
        return RequestMapper.toRequestResponseDto(requests, allItemsResponse);
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

    private void parseRepositoryRequestsWithItemsObjects(List<Object[]> objects, List<Item> items, List<Request> requests) {
        for (Object[] obj : objects) {
            Item item = (Item) obj[0];
            if (item != null) {
                items.add(item);
            }
            Request req = (Request) obj[1];
            requests.add(req);
        }
    }
}
