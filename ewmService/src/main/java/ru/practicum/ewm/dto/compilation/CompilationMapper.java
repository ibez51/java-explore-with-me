package ru.practicum.ewm.dto.compilation;

import org.mapstruct.*;
import ru.practicum.ewm.dto.event.EventMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompilationMapper {
    @Mapping(source = "eventList", target = "events")
    CompilationDto toCompilationDto(Compilation compilation, List<Event> eventList);

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> eventList);

    @Mapping(target = "eventList", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "pinned", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "title", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateCompilation(UpdateCompilationRequest updateCompilationRequest, @MappingTarget Compilation compilation, List<Event> eventList);
}
