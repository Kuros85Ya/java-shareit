package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestResponseDTO {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<RequestedItemResponseDto> items;
}