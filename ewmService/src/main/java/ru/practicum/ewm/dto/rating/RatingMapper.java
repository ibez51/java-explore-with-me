package ru.practicum.ewm.dto.rating;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Rating;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring", uses = {RatingIdMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RatingMapper {
    @Mapping(source = "event.id", target = "ratingId.eventId")
    @Mapping(source = "owner.id", target = "ratingId.ownerId")
    Rating toRating(Event event, User owner, Integer ratingValue);
}
