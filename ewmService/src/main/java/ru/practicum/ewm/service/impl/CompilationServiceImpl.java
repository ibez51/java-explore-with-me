package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.CompilationRepository;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.CompilationMapper;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned,
                                                Integer from,
                                                Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Compilation> compilationList = pinned ?
                compilationRepository.findByPinnedOrderByIdAsc(pinned, page).getContent() :
                compilationRepository.findAll(page).getContent();

        return compilationList.stream()
                .map(x -> compilationMapper.toCompilationDto(x, x.getEventList()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto getCompilationById(Integer compilationId) {
        Compilation compilation = findCompilationById(compilationId);

        return compilationMapper.toCompilationDto(compilation, compilation.getEventList());
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventService.findAllEventsById(newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, eventList);

        compilationRepository.save(compilation);

        return compilationMapper.toCompilationDto(compilation, compilation.getEventList());
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compilationId) {
        if (!compilationRepository.isCompilationExists(compilationId)) {
            throw new NullPointerException("Compilation with id=" + compilationId + " was not found");
        }

        compilationRepository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Integer compilationId,
                                            UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilationById(compilationId);

        List<Event> eventList = Objects.nonNull(updateCompilationRequest.getEvents()) ?
                eventService.findAllEventsById(updateCompilationRequest.getEvents()) :
                null;

        compilationMapper.updateCompilation(updateCompilationRequest, compilation, eventList);
        compilation = compilationRepository.save(compilation);

        return compilationMapper.toCompilationDto(compilation, compilation.getEventList());
    }

    @Override
    public Compilation findCompilationById(Integer compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NullPointerException("Category with id=" + compilationId + " was not found"));
    }
}
