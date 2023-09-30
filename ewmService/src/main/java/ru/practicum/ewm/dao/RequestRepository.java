package ru.practicum.ewm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByEventId(Integer eventId);

    List<Request> findByEventIdAndStatusOrderByIdAsc(Integer eventId, RequestStatus requestStatus);

    List<Request> findByRequesterIdOrderByIdAsc(Integer userId);

    Optional<Request> findByIdAndRequesterId(Integer requestId, Integer userId);

    @Query(value = "select requests.event_id, count(*) from requests requests " +
            "where requests.status = 'CONFIRMED' " +
            "and  requests.event_id in (:eventList) " +
            "group by requests.event_id ",
            nativeQuery = true)
    List<Object[]> findConfirmedReqByEvent(@Param("eventList") List<Integer> eventList);

    @Query(value = "select case when count(*) > 0 then true else false end from requests requests " +
            "where requests.requester_id = :userId " +
            "and requests.event_id = :eventId",
            nativeQuery = true)
    boolean isRequestByUserAndEventExists(@Param("userId") Integer userId,
                                          @Param("eventId") Integer eventId);

    @Query(value = "select case when count(*) > 0 then true else false end from requests requests " +
            "where requests.requester_id = :userId " +
            "and requests.event_id = :eventId " +
            "and requests.status = :status ",
            nativeQuery = true)
    boolean isRequestByUserAndEventAndStatusExists(@Param("userId") Integer userId,
                                                   @Param("eventId") Integer eventId,
                                                   @Param("status") String status);
}
