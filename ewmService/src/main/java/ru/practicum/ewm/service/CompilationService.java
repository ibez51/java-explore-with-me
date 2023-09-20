package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.model.Compilation;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned,
                                         Integer from,
                                         Integer size);

    CompilationDto getCompilationById(Integer compilationId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Integer compilationId);

    CompilationDto updateCompilation(Integer compilationId,
                                     UpdateCompilationRequest updateCompilationRequest);

    Compilation findCompilationById(Integer compilationId);
}
