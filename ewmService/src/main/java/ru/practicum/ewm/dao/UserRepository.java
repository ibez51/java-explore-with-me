package ru.practicum.ewm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findByIdInOrderByIdAsc(List<Integer> ids, Pageable page);

    @Query(value = "select case when count(*) > 0 then true else false end from users users " +
            "where users.id = :userId ",
            nativeQuery = true)
    boolean isUserExists(@Param("userId") Integer userId);
}
