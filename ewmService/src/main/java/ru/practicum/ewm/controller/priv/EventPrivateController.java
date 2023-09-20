package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEventsByInitiator(@PathVariable(name = "userId") Integer userId,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllEventsByInitiator(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") Integer userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(name = "userId") Integer userId,
                                     @PathVariable(name = "eventId") Integer eventId) {
        return eventService.getEventByUserAndId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(name = "userId") Integer userId,
                                    @PathVariable(name = "eventId") Integer eventId,
                                    @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllParticipationRequestForEvent(@PathVariable(name = "userId") Integer userId,
                                                                            @PathVariable(name = "eventId") Integer eventId) {
        return eventService.getAllParticipationRequestForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateRequestStatus(@PathVariable(name = "userId") Integer userId,
                                                                 @PathVariable(name = "eventId") Integer eventId,
                                                                 @Valid @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        return eventService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequestDto);
    }
}
