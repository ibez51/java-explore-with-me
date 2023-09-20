package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.CategoryRepository;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryMapper;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.isCategoryExists(categoryId)) {
            throw new NullPointerException("Category with id=" + categoryId + " was not found");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer categoryId,
                                      NewCategoryDto newCategoryDto) {
        Category category = findCategoryById(categoryId);

        return categoryMapper.toDto(categoryRepository.save(categoryMapper.updateCategory(newCategoryDto, category)));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from,
                                           Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Category> categoryList = categoryRepository.findAll(page).getContent();

        return categoryList.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer categoryId) {
        return categoryMapper.toDto(findCategoryById(categoryId));
    }

    @Override
    public Category findCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NullPointerException("Category with id=" + categoryId + " was not found"));
    }
}
