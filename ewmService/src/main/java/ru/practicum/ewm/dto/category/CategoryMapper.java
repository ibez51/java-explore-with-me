package ru.practicum.ewm.dto.category;

import org.mapstruct.*;
import ru.practicum.ewm.model.Category;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toCategory(NewCategoryDto newCategoryDto);

    @Mapping(target = "name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    Category updateCategory(NewCategoryDto newCategoryDto, @MappingTarget Category category);
}
