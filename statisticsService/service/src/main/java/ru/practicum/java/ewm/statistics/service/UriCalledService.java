package ru.practicum.java.ewm.statistics.service;

import ru.practicum.java.ewm.statistics.dto.UriCalledDto;
import ru.practicum.java.ewm.statistics.dto.UriCalledStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UriCalledService {
    void saveUriCall(UriCalledDto uriCalledDto);

    List<UriCalledStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uriList, boolean isUnique);
}
