package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {

    Request create(Integer userId, RequestRequestDTO request);

    List<RequestResponseDTO> getItemsThatWereCreatedByRequest(Integer userId);

    List<RequestResponseDTO> getAllRequestsPageable(Integer userId, Integer from, Integer size);

    RequestResponseDTO getSingleRequestById(Integer requestId, Integer userId);
}
