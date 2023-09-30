package ru.practicum.ewm.dto.rating;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.RatingId;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RatingIdMapper {
    RatingId toRatingId(Event event, User owner);
}
