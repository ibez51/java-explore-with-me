package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Integer categoryId);

    CategoryDto updateCategory(Integer categoryId,
                               NewCategoryDto newCategoryDto);

    List<CategoryDto> getCategories(Integer from,
                                    Integer size);

    CategoryDto getCategoryById(Integer categoryId);

    Category findCategoryById(Integer categoryId);
}
