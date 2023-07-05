package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Пользователь приложения
 * id — уникальный идентификатор пользователя
 * name — имя или логин пользователя
 * email — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
 **/
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String name;
    @Email
    @NotNull
    private String email;
}
