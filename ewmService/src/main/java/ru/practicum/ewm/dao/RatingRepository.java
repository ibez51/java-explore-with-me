package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Rating;
import ru.practicum.ewm.model.RatingId;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    @Query(value = "select rating.event_id, sum(rating_value), count(*) from event_rating rating " +
            "where rating.event_id in (:eventIdList) " +
            "group by rating.event_id ",
            nativeQuery = true)
    List<Object[]> calcRateForEventList(@Param("eventIdList") List<Integer> eventIdList);

    @Query(value = "select events.initiator, sum(rating_value), count(*) from event_rating rating " +
            "inner join events events " +
            "on events.id = rating.event_id " +
            "where events.initiator in (:userIdList) " +
            "group by events.initiator ",
            nativeQuery = true)
    List<Object[]> calcRateForUserList(@Param("userIdList") List<Integer> userIdList);
}
