package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.model.EventSortType;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(name = "text", required = false) String text,
                                            @RequestParam(name = "categories", required = false) List<Integer> categories,
                                            @RequestParam(name = "paid", required = false) Boolean paid,
                                            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd' 'HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(name = "sort", required = false) EventSortType sort,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                            HttpServletRequest httpServletRequest) {
        return eventService.getAllEvent(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable(name = "eventId") Integer eventId,
                                     HttpServletRequest httpServletRequest) {
        return eventService.getPublicEventById(eventId, httpServletRequest);
    }
}
