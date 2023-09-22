package ru.practicum.ewm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ParticipationRequestDto {
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime created;
    private Integer event;
    private Integer id;
    private Integer requester;
    private RequestStatus status;
}
