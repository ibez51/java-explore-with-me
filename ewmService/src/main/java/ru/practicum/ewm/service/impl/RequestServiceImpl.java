package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.RequestRepository;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<ParticipationRequestDto> getRequestByUser(Integer userId) {
        return requestRepository.findByRequesterIdOrderByIdAsc(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Integer userId,
                                                 Integer eventId) {
        if (requestRepository.isRequestByUserAndEventExists(userId, eventId)) {
            throw new DataIntegrityViolationException("Can't create duplicate request for eventId=" + eventId + " and userId=" + userId);
        }

        Event event = eventService.findEventById(eventId);
        User initiator = event.getInitiator();

        if (initiator.getId().equals(userId)) {
            throw new DataIntegrityViolationException("You can't create request for your own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("Event not published yet");
        }

        if (event.getParticipantLimit() > 0
                && eventService.findConfirmedReqByEvent(List.of(eventId)).getOrDefault(eventId, 0) >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException("Participants limit for this event is reached");
        }

        User user = userService.findUserById(userId);

        Request request = requestMapper.toRequest(user, event);

        request = request.setStatus(event.getRequestModeration()
                && event.getParticipantLimit() != 0 ?
                RequestStatus.PENDING :
                RequestStatus.CONFIRMED);

        request = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Integer userId,
                                                 Integer requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NullPointerException("Request with id=" + requestId + " was not found"));

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(request);
    }
}
