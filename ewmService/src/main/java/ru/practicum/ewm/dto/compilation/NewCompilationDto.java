package ru.practicum.ewm.dto.compilation;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class NewCompilationDto {
    private List<Integer> events = new ArrayList<>();
    private Boolean pinned = false;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
