package ru.practicum.ewm.dto.event;

import org.mapstruct.*;
import ru.practicum.ewm.dto.category.CategoryMapper;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {
    @Mapping(source = "category", target = "category")
    @Mapping(source = "event.id", target = "id")
    EventShortDto toShortDto(Event event, Category category, User initiator, Integer confirmedRequests, Integer views);

    @Mapping(source = "userId", target = "initiator.id")
    @Mapping(source = "location.lat", target = "locationLat")
    @Mapping(source = "location.lon", target = "locationLon")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "compilationList", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "id", ignore = true)
    Event toEvent(Integer userId, NewEventDto newEventDto, Location location, Category category);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "event.locationLat", target = "location.lat")
    @Mapping(source = "event.locationLon", target = "location.lon")
    @Mapping(source = "event.id", target = "id")
    EventFullDto toFullDto(Event event, Category category, User initiator, Integer confirmedRequests, Integer views);

    @Mapping(target = "annotation", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", source = "category", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "locationLat", source = "updateEventUserRequestDto.location.lat", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "locationLon", source = "updateEventUserRequestDto.location.lon", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "paid", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "participantLimit", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestModeration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "state", source = "eventState", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "compilationList", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEvent(UpdateEventUserRequestDto updateEventUserRequestDto, Category category, EventState eventState, @MappingTarget Event event);

    @Mapping(target = "annotation", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", source = "category", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "eventDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "locationLat", source = "updateEventAdminRequestDto.location.lat", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "locationLon", source = "updateEventAdminRequestDto.location.lon", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "paid", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "participantLimit", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "requestModeration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "state", source = "eventState", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "compilationList", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEvent(UpdateEventAdminRequestDto updateEventAdminRequestDto, Category category, EventState eventState, LocalDateTime publishedOn, @MappingTarget Event event);

    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "rejectedRequests", target = "rejectedRequests")
    EventRequestStatusUpdateResultDto toEventRequestStatusUpdateResultDto(Boolean dummy,
                                                                          List<ParticipationRequestDto> confirmedRequests,
                                                                          List<ParticipationRequestDto> rejectedRequests);
}
