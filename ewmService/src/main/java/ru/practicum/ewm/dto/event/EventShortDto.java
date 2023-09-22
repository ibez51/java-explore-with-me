package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime eventDate;
    private Integer id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
