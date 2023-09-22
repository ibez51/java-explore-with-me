package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.model.EventStateUserAction;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UpdateEventUserRequestDto {
    @Size(min = 20, max = 2000)
    private String annotation;
    private Integer category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateUserAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
