package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    @Query("select i, r from Request r left join Item i on i.request = r where r.requestor = ?1 order by r.created desc")
    List<Object[]> findRequestsWithItemsByUser(User requestor);

    @Query("select i, r from Request r left join Item i on i.request = r where r.requestor <> ?1 order by r.created desc")
    List<Object[]> findAllRequestsOfOtherUsersWithItemsPageable(User requestor, PageRequest pageable);
}
