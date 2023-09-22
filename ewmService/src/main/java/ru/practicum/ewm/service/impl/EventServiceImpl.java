package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.EventRepository;
import ru.practicum.ewm.dao.RequestRepository;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.request.RequestMapper;
import ru.practicum.ewm.exceptions.InvalidParameterException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.specification.EventQuerySpecification;
import ru.practicum.ewm.statistics.client.ServiceHTTPClient;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryService categoryService;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final ServiceHTTPClient serviceHTTPClient;

    @Override
    public List<EventShortDto> getAllEventsByInitiator(Integer userId,
                                                       Integer from,
                                                       Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Event> eventList = eventRepository.findByInitiatorOrderByIdAsc(userId, page).getContent();

        List<Integer> eventIdList = eventList.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(eventIdList);
        Map<Integer, Integer> viewMap = getViewStatistics(eventIdList, true);

        return eventList.stream()
                .map(x -> eventMapper.toShortDto(x,
                        x.getCategory(),
                        x.getInitiator(),
                        confirmedRequestsMap.getOrDefault(x.getId(), 0),
                        viewMap.getOrDefault(x.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Integer userId,
                                    NewEventDto newEventDto) {
        Category category = categoryService.findCategoryById(newEventDto.getCategory());

        Event event = eventMapper.toEvent(userId, newEventDto, newEventDto.getLocation(), category);
        event = eventRepository.save(event);

        return eventMapper.toFullDto(event, event.getCategory(), event.getInitiator(), 0, 0);
    }

    @Override
    public EventFullDto getEventByUserAndId(Integer userId,
                                            Integer eventId) {
        Event event = findByIdAndInitiator(eventId, userId);

        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(List.of(eventId));
        Map<Integer, Integer> viewMap = getViewStatistics(List.of(eventId), true);

        return eventMapper.toFullDto(event,
                event.getCategory(),
                event.getInitiator(),
                confirmedRequestsMap.getOrDefault(eventId, 0),
                viewMap.getOrDefault(eventId, 0));
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Integer userId,
                                    Integer eventId,
                                    UpdateEventUserRequestDto updateEventUserRequestDto) {
        if (Objects.nonNull(updateEventUserRequestDto.getEventDate())
                && updateEventUserRequestDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new DataIntegrityViolationException("Event date can't be less than 2 hours from now");
        }

        Event event = findByIdAndInitiator(eventId, userId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new DataIntegrityViolationException("You can't change published event.");
        }

        Category category = null;
        if (Objects.nonNull(updateEventUserRequestDto.getCategory())) {
            category = categoryService.findCategoryById(updateEventUserRequestDto.getCategory());
        }

        EventState eventStateUpd = null;
        if (Objects.nonNull(updateEventUserRequestDto.getStateAction())) {
            switch (updateEventUserRequestDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    if (event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
                        throw new DataIntegrityViolationException("Event publish date can't be less than 2 hours of event date");
                    }
                    eventStateUpd = EventState.PENDING;
                    break;
                case CANCEL_REVIEW:
                    eventStateUpd = EventState.CANCELED;
                    break;
            }
        }

        eventMapper.updateEvent(updateEventUserRequestDto, category, eventStateUpd, event);
        event = eventRepository.save(event);

        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(List.of(eventId));
        Map<Integer, Integer> viewMap = getViewStatistics(List.of(eventId), true);

        return eventMapper.toFullDto(event,
                event.getCategory(),
                event.getInitiator(),
                confirmedRequestsMap.getOrDefault(eventId, 0),
                viewMap.getOrDefault(eventId, 0));
    }

    @Override
    public List<ParticipationRequestDto> getAllParticipationRequestForEvent(Integer userId, Integer eventId) {
        findByIdAndInitiator(eventId, userId);

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateRequestStatus(Integer userId,
                                                                 Integer eventId,
                                                                 EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        Event event = findByIdAndInitiator(eventId, userId);

        if (event.getParticipantLimit() > 0
                && event.getRequestModeration()) {

            if (event.getParticipantLimit() <= findConfirmedReqByEvent(List.of(eventId)).getOrDefault(eventId, 0)) {
                throw new DataIntegrityViolationException("Limit of participants for event " + eventId + "is reached");
            }

            for (Integer requestId : eventRequestStatusUpdateRequestDto.getRequestIds()) {
                Request request = requestRepository.findById(requestId).orElse(null);

                if (Objects.nonNull(request)) {
                    if (eventRequestStatusUpdateRequestDto.getStatus() == RequestStatus.REJECTED
                            && request.getStatus() == RequestStatus.CONFIRMED) {
                        throw new DataIntegrityViolationException("Can not reject confirmed request " + request.getId());
                    }

                    request.setStatus(eventRequestStatusUpdateRequestDto.getStatus());

                    requestRepository.save(request);
                }
            }
        }

        List<ParticipationRequestDto> confirmedRequestList = requestRepository.findByEventIdAndStatusOrderByIdAsc(eventId, RequestStatus.CONFIRMED).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> rejectedRequestList = requestRepository.findByEventIdAndStatusOrderByIdAsc(eventId, RequestStatus.REJECTED).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return eventMapper.toEventRequestStatusUpdateResultDto(false,
                confirmedRequestList,
                rejectedRequestList);
    }

    @Override
    public List<EventFullDto> findEvents(List<Integer> users,
                                         List<EventState> states,
                                         List<Integer> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Event> events = eventRepository.findAll(EventQuerySpecification.findEventsByAdminParameters(users, states, categories, rangeStart, rangeEnd), page).getContent();

        List<Integer> eventIdList = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(eventIdList);
        Map<Integer, Integer> viewMap = getViewStatistics(eventIdList, true);

        return events.stream()
                .map(x -> eventMapper.toFullDto(x,
                        x.getCategory(),
                        x.getInitiator(),
                        confirmedRequestsMap.getOrDefault(x.getId(), 0),
                        viewMap.getOrDefault(x.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Integer eventId,
                                    UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        if (Objects.nonNull(updateEventAdminRequestDto.getEventDate())
                && updateEventAdminRequestDto.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new DataIntegrityViolationException("Event date can't be less than hour from now");
        }

        Event event = findEventById(eventId);

        EventState eventStateUpd = null;
        LocalDateTime publishedOnDateTime = null;
        if (Objects.nonNull(updateEventAdminRequestDto.getStateAction())) {
            switch (updateEventAdminRequestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
                        throw new DataIntegrityViolationException("Event publish date can't be less than hour of event date");
                    }
                    if (event.getState() != EventState.PENDING) {
                        throw new DataIntegrityViolationException("Cannot publish event in state " + event.getState());
                    }
                    eventStateUpd = EventState.PUBLISHED;
                    publishedOnDateTime = LocalDateTime.now();
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new DataIntegrityViolationException("Cannot reject event in state " + event.getState());
                    }
                    eventStateUpd = EventState.CANCELED;
                    break;
            }
        }

        Category category = null;
        if (Objects.nonNull(updateEventAdminRequestDto.getCategory())) {
            category = categoryService.findCategoryById(updateEventAdminRequestDto.getCategory());
        }

        eventMapper.updateEvent(updateEventAdminRequestDto,
                category,
                eventStateUpd,
                publishedOnDateTime,
                event);
        event = eventRepository.save(event);

        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(List.of(eventId));
        Map<Integer, Integer> viewMap = getViewStatistics(List.of(eventId), true);

        return eventMapper.toFullDto(event,
                event.getCategory(),
                event.getInitiator(),
                confirmedRequestsMap.getOrDefault(eventId, 0),
                viewMap.getOrDefault(eventId, 0));
    }

    @Override
    public List<EventShortDto> getAllEvent(String text,
                                           List<Integer> categories,
                                           Boolean paid,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           Boolean onlyAvailable,
                                           EventSortType sort,
                                           Integer from,
                                           Integer size,
                                           HttpServletRequest httpServletRequest) {
        if (Objects.nonNull(rangeStart)
                && Objects.nonNull(rangeEnd)
                && rangeStart.isAfter(rangeEnd)) {
            throw new InvalidParameterException("Start date must be before end date");
        }

        List<Event> events = eventRepository.findAll(EventQuerySpecification.findEventsByPublicParameters(text, categories, paid, rangeStart, rangeEnd, sort));

        List<Integer> eventIdList = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(eventIdList);
        Map<Integer, Integer> viewMap = getViewStatistics(eventIdList, true);

        if (Objects.nonNull(onlyAvailable)
                && onlyAvailable) {
            events = events.stream()
                    .filter(x -> x.getParticipantLimit() > 0
                            && x.getParticipantLimit() <= confirmedRequestsMap.getOrDefault(x.getId(), 0))
                    .collect(Collectors.toList());
        }

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(x -> eventMapper.toShortDto(x,
                        x.getCategory(),
                        x.getInitiator(),
                        confirmedRequestsMap.getOrDefault(x.getId(), 0),
                        viewMap.getOrDefault(x.getId(), 0)))
                .collect(Collectors.toList());

        if (Objects.nonNull(sort)
                && sort == EventSortType.VIEWS) {
            eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews));
        }

        eventShortDtoList = eventShortDtoList.subList(from,
                Math.min(from + size, eventShortDtoList.size()));

        saveStatistics(eventShortDtoList.stream().map(EventShortDto::getId).collect(Collectors.toList()), false, httpServletRequest);
        saveStatistics(null, true, httpServletRequest);

        return eventShortDtoList;
    }

    @Override
    public EventFullDto getPublicEventById(Integer eventId, HttpServletRequest httpServletRequest) {
        Event event = findEventByIdAndState(eventId, EventState.PUBLISHED);

        Map<Integer, Integer> confirmedRequestsMap = findConfirmedReqByEvent(List.of(eventId));
        Map<Integer, Integer> viewMap = getViewStatistics(List.of(eventId), true);

        saveStatistics(List.of(eventId), false, httpServletRequest);

        return eventMapper.toFullDto(event,
                event.getCategory(),
                event.getInitiator(),
                confirmedRequestsMap.getOrDefault(eventId, 0),
                viewMap.getOrDefault(eventId, 0));
    }

    @Override
    public Event findEventByIdAndState(Integer eventId,
                                       EventState state) {
        return eventRepository.findByIdAndStateOrderByIdAsc(eventId, state)
                .orElseThrow(() -> new NullPointerException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public Event findEventById(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NullPointerException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<Event> findAllEventsById(List<Integer> eventIdList) {
        return eventRepository.findAllById(eventIdList);
    }

    @Override
    public Map<Integer, Integer> findConfirmedReqByEvent(List<Integer> eventIdList) {
        if (Objects.isNull(eventIdList)
                || eventIdList.size() == 0) {
            return Map.of();
        }
        List<Object[]> list = requestRepository.findConfirmedReqByEvent(eventIdList);

        return list.stream()
                .collect(Collectors.toMap(
                        x -> ((BigInteger) x[0]).intValue(),
                        x -> ((BigInteger) x[1]).intValue()
                ));
    }

    public Event findByIdAndInitiator(Integer eventId,
                                      Integer userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NullPointerException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public Map<Integer, Integer> getViewStatistics(List<Integer> eventIdList,
                                                   boolean unique) {
        List<String> uriList = eventIdList.stream().map(x -> "/events/" + x).collect(Collectors.toList());
        List<UriCalledStatisticDto> statResult = List.of();

        ResponseEntity<List<UriCalledStatisticDto>> statResponse = serviceHTTPClient.getStatistics(LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now(),
                uriList,
                unique);

        if (statResponse.getStatusCode().is2xxSuccessful()
                && statResponse.hasBody()) {
            statResult = statResponse.getBody();
        }

        return Objects.nonNull(statResult) ?
                statResult.stream()
                        .collect(Collectors.toMap(
                                x -> {
                                    Matcher matcher = Pattern.compile("/events/(.*)").matcher(x.getUri());
                                    return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
                                },
                                x -> (int) x.getHits())) :
                new HashMap<>();
    }

    private void saveStatistics(List<Integer> eventIdList,
                                Boolean commonUri,
                                HttpServletRequest httpServletRequest) {
        List<String> uriList;
        if (commonUri) {
            uriList = List.of("/events");
        } else {
            uriList = eventIdList.stream().map(x -> "/events/" + x).collect(Collectors.toList());
        }

        uriList.forEach(uri -> serviceHTTPClient.postHitUri(new UriCalledDto()
                .setApp("ewm")
                .setUri(uri)
                .setIp(httpServletRequest.getRemoteAddr())
                .setTimestamp(LocalDateTime.now())));
    }
}
