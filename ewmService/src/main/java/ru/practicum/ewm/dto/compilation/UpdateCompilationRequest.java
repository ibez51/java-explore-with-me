package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateCompilationRequest {
    private List<Integer> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
