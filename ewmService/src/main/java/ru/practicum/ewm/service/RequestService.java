package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestByUser(Integer userId);

    ParticipationRequestDto createRequest(Integer userId,
                                          Integer eventId);

    ParticipationRequestDto cancelRequest(Integer userId,
                                          Integer requestId);
}
