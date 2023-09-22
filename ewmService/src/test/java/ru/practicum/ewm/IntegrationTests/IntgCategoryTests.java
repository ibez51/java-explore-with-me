package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.service.CategoryService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgCategoryTests {
    @Autowired
    private final CategoryService categoryService;

    @Test
    void contextLoads() {
        assertThat(categoryService).isNotNull();
    }

    @Test
    public void testCategoryService() {
        NewCategoryDto newCategoryDto = new NewCategoryDto()
                .setName("Category name");

        CategoryDto categoryDto = categoryService.createCategory(newCategoryDto);
        assertThat(categoryDto.getId()).isNotNull();

        Category category = categoryService.findCategoryById(categoryDto.getId());
        assertThat(category.getId()).isEqualTo(categoryDto.getId());

        categoryService.deleteCategory(categoryDto.getId());

        assertThrows(NullPointerException.class, () -> categoryService.findCategoryById(category.getId()));

        newCategoryDto = newCategoryDto.setName("Category name2");
        categoryDto = categoryService.createCategory(newCategoryDto);

        assertThat(categoryDto.getId()).isEqualTo(categoryService.getCategoryById(categoryDto.getId()).getId());

        CategoryDto categoryDto1 = categoryService.createCategory(new NewCategoryDto()
                .setName("Category name1"));

        assertEquals(2, categoryService.getCategories(0, 2).size());

        newCategoryDto = new NewCategoryDto().setName("New category name");
        categoryService.updateCategory(categoryDto1.getId(), newCategoryDto);

        assertEquals(newCategoryDto.getName(), categoryService.getCategoryById(categoryDto1.getId()).getName());
    }
}
