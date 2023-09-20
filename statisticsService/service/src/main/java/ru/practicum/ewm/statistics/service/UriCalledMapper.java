package ru.practicum.ewm.statistics.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;
import ru.practicum.ewm.statistics.service.model.UriCalled;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UriCalledMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calledDateTime", source = "timestamp")
    UriCalled toUriCalled(UriCalledDto uriCalledDto);

    @Mapping(target = "hits", source = "id")
    UriCalledStatisticDto toUriCalledStatisticsDto(UriCalled uriCalled);
}
