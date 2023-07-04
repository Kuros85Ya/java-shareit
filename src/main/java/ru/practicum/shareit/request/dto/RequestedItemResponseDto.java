package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestedItemResponseDto {
        private Integer id;
        private String name;
        private String description;
        private Boolean available;
        private Integer requestId;
}
