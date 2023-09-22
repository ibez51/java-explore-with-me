package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventSortType;
import ru.practicum.ewm.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {
    List<EventShortDto> getAllEventsByInitiator(Integer userId,
                                                Integer from,
                                                Integer size);

    EventFullDto createEvent(Integer userId,
                             NewEventDto newEventDto);

    EventFullDto getEventByUserAndId(Integer userId,
                                     Integer eventId);

    EventFullDto updateEvent(Integer userId,
                             Integer eventId,
                             UpdateEventUserRequestDto updateEventUserRequestDto);

    List<ParticipationRequestDto> getAllParticipationRequestForEvent(Integer userId,
                                                                     Integer eventId);

    EventRequestStatusUpdateResultDto updateRequestStatus(Integer userId,
                                                          Integer eventId,
                                                          EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);

    List<EventFullDto> findEvents(List<Integer> users,
                                  List<EventState> states,
                                  List<Integer> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Integer from,
                                  Integer size);

    EventFullDto updateEvent(Integer eventId,
                             UpdateEventAdminRequestDto updateEventAdminRequestDto);

    List<EventShortDto> getAllEvent(String text,
                                    List<Integer> categories,
                                    Boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Boolean onlyAvailable,
                                    EventSortType sort,
                                    Integer from,
                                    Integer size,
                                    HttpServletRequest httpServletRequest);

    EventFullDto getPublicEventById(Integer eventId,
                                    HttpServletRequest httpServletRequest);

    Event findEventByIdAndState(Integer eventId,
                                EventState state);

    Event findEventById(Integer eventId);

    List<Event> findAllEventsById(List<Integer> eventIdList);

    Map<Integer, Integer> findConfirmedReqByEvent(List<Integer> eventIdList);

    Map<Integer, Integer> getViewStatistics(List<Integer> eventIdList,
                                            boolean unique);
}
