package ru.practicum.ewm.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.admin.CategoryAdminController;
import ru.practicum.ewm.controller.pub.CategoryPublicController;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CategoryAdminController.class, CategoryPublicController.class})
public class RESTCategoryTests {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    CategoryService categoryService;
    @Autowired
    private MockMvc mvc;

    private final CategoryDto categoryDto = new CategoryDto()
            .setId(1)
            .setName("Name");

    @Test
    public void testCreateCategory() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto()
                .setName("");

        doReturn(categoryDto)
                .when(categoryService)
                .createCategory(any(NewCategoryDto.class));

        mvc.perform(post("/admin/categories")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isBadRequest());

        newCategoryDto.setName("Name");
        mvc.perform(post("/admin/categories")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        doNothing()
                .when(categoryService)
                .deleteCategory(anyInt());

        mvc.perform(delete("/admin/categories/")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isInternalServerError());

        mvc.perform(delete("/admin/categories/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateCategory() throws Exception {
        NewCategoryDto newCategoryDto = new NewCategoryDto()
                .setName("");

        doReturn(categoryDto)
                .when(categoryService)
                .updateCategory(anyInt(), any(NewCategoryDto.class));

        mvc.perform(patch("/admin/categories/")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isInternalServerError());

        mvc.perform(patch("/admin/categories/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isInternalServerError());

        mvc.perform(patch("/admin/categories/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isBadRequest());

        newCategoryDto.setName("Name");
        mvc.perform(patch("/admin/categories/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCategoryDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCategories() throws Exception {
        doReturn(List.of(categoryDto))
                .when(categoryService)
                .getCategories(anyInt(), anyInt());

        mvc.perform(get("/categories")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(categoryDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(categoryDto.getName())));

        mvc.perform(get("/categories?size=1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());

        mvc.perform(get("/categories?from=0")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());


        mvc.perform(get("/categories?size=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/categories?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetCategoryById() throws Exception {
        doReturn(categoryDto)
                .when(categoryService)
                .getCategoryById(anyInt());

        mvc.perform(get("/categories/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }
}
