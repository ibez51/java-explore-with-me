package ru.practicum.ewm.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.admin.CompilationAdminController;
import ru.practicum.ewm.controller.pub.CompilationPublicController;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.service.CompilationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CompilationAdminController.class, CompilationPublicController.class})
public class RESTCompilationTests {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    CompilationService compilationService;
    @Autowired
    private MockMvc mvc;
    private final CompilationDto compilationDto = new CompilationDto()
            .setEvents(List.of(new EventShortDto()
                    .setAnnotation("Annotation")
                    .setCategory(new CategoryDto()
                            .setId(1)
                            .setName("CategoryName"))
                    .setConfirmedRequests(10)
                    .setEventDate(LocalDateTime.now())
                    .setId(1)
                    .setInitiator(new UserShortDto()
                            .setId(1)
                            .setName("Name"))
                    .setPaid(true)
                    .setTitle("Title")
                    .setViews(10)))
            .setId(1)
            .setPinned(true)
            .setTitle("Title");

    @Test
    public void testCreateCompilation() throws Exception {
        doReturn(compilationDto)
                .when(compilationService)
                .createCompilation(any(NewCompilationDto.class));

        NewCompilationDto newCompilationDto = new NewCompilationDto()
                .setEvents(List.of(1, 2, 3))
                .setPinned(true)
                .setTitle("Title");

        mvc.perform(post("/admin/compilations")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.events.[0].annotation", is(compilationDto.getEvents().get(0).getAnnotation())))
                .andExpect(jsonPath("$.events.[0].category.id", is(compilationDto.getEvents().get(0).getCategory().getId()), Integer.class))
                .andExpect(jsonPath("$.events.[0].category.name", is(compilationDto.getEvents().get(0).getCategory().getName())))
                .andExpect(jsonPath("$.events.[0].confirmedRequests", is(compilationDto.getEvents().get(0).getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.events.[0].eventDate", is(compilationDto.getEvents().get(0).getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.events.[0].id", is(compilationDto.getEvents().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$.events.[0].initiator.id", is(compilationDto.getEvents().get(0).getInitiator().getId()), Integer.class))
                .andExpect(jsonPath("$.events.[0].initiator.name", is(compilationDto.getEvents().get(0).getInitiator().getName())))
                .andExpect(jsonPath("$.events.[0].paid", is(compilationDto.getEvents().get(0).getPaid())))
                .andExpect(jsonPath("$.events.[0].title", is(compilationDto.getEvents().get(0).getTitle())))
                .andExpect(jsonPath("$.events.[0].views", is(compilationDto.getEvents().get(0).getViews()), Integer.class))
                .andExpect(jsonPath("$.pinned", is(compilationDto.getPinned())))
                .andExpect(jsonPath("$.title", is(compilationDto.getTitle())));
    }

    @Test
    public void testDeleteCompilation() throws Exception {
        doNothing()
                .when(compilationService)
                .deleteCompilation(anyInt());

        mvc.perform(delete("/admin/compilations/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateCompilation() throws Exception {
        doReturn(compilationDto)
                .when(compilationService)
                .updateCompilation(anyInt(), any(UpdateCompilationRequest.class));

        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest()
                .setEvents(List.of(1, 2, 3))
                .setPinned(true)
                .setTitle("Title");

        mvc.perform(patch("/admin/compilations/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateCompilationRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCompilations() throws Exception {
        doReturn(List.of(compilationDto))
                .when(compilationService)
                .getCompilations(anyBoolean(), anyInt(), anyInt());

        mvc.perform(get("/compilations")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());

        mvc.perform(get("/compilations?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/compilations?size=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetCompilationById() throws Exception {
        doReturn(compilationDto)
                .when(compilationService)
                .getCompilationById(anyInt());

        mvc.perform(get("/compilations/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }
}
