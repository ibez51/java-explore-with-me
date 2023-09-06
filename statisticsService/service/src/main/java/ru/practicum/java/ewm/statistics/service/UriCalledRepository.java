package ru.practicum.java.ewm.statistics.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.java.ewm.statistics.service.model.UriCalled;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UriCalledRepository extends JpaRepository<UriCalled, Long> {
    @Query(value = "select application_name, uri, count(ip) as id, NOW() as called_datetime, '' as ip from " +
            "(select distinct application_name, ip, uri from uri_called_log " +
            "where uri in (:uriList) " +
            "and called_datetime >= :start " +
            "and called_datetime <= :end) as dist " +
            "group by application_name, uri " +
            "order by count(ip) desc"
            , nativeQuery = true)
    List<UriCalled> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uriList);

    @Query(value = "select application_name, uri, count(ip) as id, NOW() as called_datetime, '' as ip from " +
            "(select distinct application_name, ip, uri from uri_called_log " +
            "where called_datetime >= :start " +
            "and called_datetime <= :end) as dist " +
            "group by application_name, uri " +
            "order by count(ip) desc "
            , nativeQuery = true)
    List<UriCalled> getUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query(value = "select application_name, uri, count(ip) AS id, NOW() as called_datetime, '' as ip from uri_called_log " +
            "where uri in (:uriList) " +
            "and called_datetime >= :start " +
            "and called_datetime <= :end " +
            "group by application_name, uri " +
            "order by count(ip) desc "
            , nativeQuery = true)
    List<UriCalled> getStats(LocalDateTime start, LocalDateTime end, List<String> uriList);

    @Query(value = "select application_name, uri, count(ip) AS id, NOW() as called_datetime, '' as ip from uri_called_log " +
            "where called_datetime >= :start " +
            "and called_datetime <= :end " +
            "group by application_name, uri " +
            "order by count(ip) desc "
            , nativeQuery = true)
    List<UriCalled> getStats(LocalDateTime start, LocalDateTime end);
}
