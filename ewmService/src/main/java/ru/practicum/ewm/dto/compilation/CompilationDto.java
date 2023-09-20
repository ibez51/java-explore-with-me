package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

@Data
@Accessors(chain = true)
public class CompilationDto {
    private List<EventShortDto> events;
    private Integer id;
    private Boolean pinned;
    private String title;
}
