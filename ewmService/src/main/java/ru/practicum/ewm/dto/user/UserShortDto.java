package ru.practicum.ewm.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserShortDto {
    private Integer id;
    private String name;
    private Integer rating;
}
