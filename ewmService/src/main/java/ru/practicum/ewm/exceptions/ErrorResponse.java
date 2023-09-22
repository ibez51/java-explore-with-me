package ru.practicum.ewm.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String status;
    private String reason;
    private String message;
    @JsonFormat(pattern = DateTimeFormatterUtility.PATTERN)
    private LocalDateTime timestamp;
}
