package ru.practicum.ewm.dto.request;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

@Data
@Accessors(chain = true)
public class EventRequestStatusUpdateRequestDto {
    private List<Integer> requestIds;
    private RequestStatus status;
}
