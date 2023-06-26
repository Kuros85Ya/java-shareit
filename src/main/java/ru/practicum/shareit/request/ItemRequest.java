package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "requests", schema = "public")
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    @ManyToOne
    private User requestor;
}
