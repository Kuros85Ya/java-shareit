package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
public class UserRequestDTO {
    private Integer id;
    private final String name;
    @Email
    private final String email;
}
