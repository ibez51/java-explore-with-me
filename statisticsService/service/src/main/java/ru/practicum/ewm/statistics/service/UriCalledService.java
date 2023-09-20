package ru.practicum.ewm.statistics.service;

import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UriCalledService {
    void saveUriCall(UriCalledDto uriCalledDto);

    List<UriCalledStatisticDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uriList, boolean isUnique);
}
