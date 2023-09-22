package ru.practicum.ewm.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {
    @Query(value = "select * from events events " +
            "where events.initiator = :userId " +
            "order by Id ASC ",
            nativeQuery = true)
    Page<Event> findByInitiatorOrderByIdAsc(@Param("userId") Integer userId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(Integer eventId, Integer userId);

    Optional<Event> findByIdAndStateOrderByIdAsc(Integer eventId, EventState state);
}
