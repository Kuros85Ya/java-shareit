package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private static final String OWNER_ID_HEADER = "X-Sharer-User-Id";
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Request create(@RequestBody @Valid RequestRequestDTO request, @RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Создаем запрос: {}", request);
        return requestService.create(userId, request);
    }

    @GetMapping()
    public List<RequestResponseDTO> getResponsesToUserRequest(@RequestHeader(OWNER_ID_HEADER) Integer userId) {
        log.info("Вывести все ответы на запрос пользователя ID = {}", userId);
        return requestService.getItemsThatWereCreatedByRequest(userId);
    }

    @GetMapping("/all")
    public List<RequestResponseDTO> getAllRequestsPageable(@RequestHeader(OWNER_ID_HEADER) Integer userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Вывести все ответы на запрос пользователя ID = {}", userId);
        return requestService.getAllRequestsPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestResponseDTO createComment(@RequestHeader(OWNER_ID_HEADER) Integer userId, @PathVariable Integer requestId) {
        log.info("Запрошены данные о вещах по одному запросу: {}", requestId);
        return requestService.getSingleRequestById(requestId, userId);
    }
}
