package ru.practicum.shareit.request.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestRequestDTO;
import ru.practicum.shareit.request.dto.RequestResponseDTO;
import ru.practicum.shareit.request.dto.RequestedItemResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {

    public static PageRequest toPageRequest(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Недопустимое значение для параметров from и size");
        }

        return PageRequest.of(from / size, size);
    }

    public static Request toRequest(RequestRequestDTO requestDTO, User requestor) {
        return new Request(null, requestDTO.getDescription(), requestor, LocalDateTime.now());
    }

    public static RequestedItemResponseDto toItemRequestResponseDto(Item item) {
        return new RequestedItemResponseDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId());
    }

    public static List<RequestResponseDTO> toRequestResponseDto(List<Request> requests, List<RequestedItemResponseDto> allItems) {
        return requests
                .stream()
                .map(request ->
                        new RequestResponseDTO(
                                request.getId(),
                                request.getDescription(),
                                request.getCreated(),
                                allItems
                                        .stream()
                                        .filter(it -> it.getRequestId().equals(request.getId()))
                                        .collect(Collectors.toList())
                        )
                )
                .distinct()
                .collect(Collectors.toList());
    }
}
