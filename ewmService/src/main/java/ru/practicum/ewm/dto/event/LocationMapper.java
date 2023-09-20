package ru.practicum.ewm.dto.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LocationMapper {
    Location toLocation(Float lat, Float lon);
}
