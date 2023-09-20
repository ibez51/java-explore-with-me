package ru.practicum.ewm.dto.event;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Location {
    private Float lat;
    private Float lon;
}
