package ru.practicum.ewm.dto.category;


import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class NewCategoryDto {
    @NotBlank
    @Size(max = 50)
    private String name;
}
