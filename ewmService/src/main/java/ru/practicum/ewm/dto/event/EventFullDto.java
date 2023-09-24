package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime eventDate;
    private Integer id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Integer views;
    private Integer rating;
}
