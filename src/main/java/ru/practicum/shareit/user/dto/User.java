package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Integer id; //— уникальный идентификатор пользователя;
    private String name; // — имя или логин пользователя;
    @Email
    @NotNull
    private String email; // — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).

    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
