package ru.practicum.ewm.dto.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDto {
    private String email;
    private Integer id;
    private String name;
}
