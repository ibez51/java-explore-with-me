package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgCompilationTests {
    @Autowired
    private final CompilationService compilationService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final EventService eventService;

    UserDto userDto1;
    UserDto userDto2;
    CategoryDto categoryDto;
    EventFullDto eventFullDto1;
    EventFullDto eventFullDto2;

    @BeforeEach
    void setUp() {
        userDto1 = userService.createUser(
                new NewUserRequestDto()
                        .setName("User1")
                        .setEmail("email1@email1.email1"));
        userDto2 = userService.createUser(new NewUserRequestDto()
                .setName("User2")
                .setEmail("email2@email2.email2"));
        categoryDto = categoryService.createCategory(new NewCategoryDto()
                .setName("Category"));
        eventFullDto1 = eventService.createEvent(userDto1.getId(), new NewEventDto()
                .setAnnotation("Annotation1")
                .setCategory(categoryDto.getId())
                .setDescription("Description1")
                .setEventDate(LocalDateTime.now().plusHours(5))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setTitle("Title1"));
        eventFullDto2 = eventService.createEvent(userDto1.getId(),
                new NewEventDto()
                        .setAnnotation("Annotation2")
                        .setCategory(categoryDto.getId())
                        .setDescription("Description2")
                        .setEventDate(LocalDateTime.now().plusHours(5))
                        .setLocation(new Location()
                                .setLat(11.11F)
                                .setLon(11.11F))
                        .setPaid(true)
                        .setParticipantLimit(10)
                        .setRequestModeration(true)
                        .setTitle("Title2"));
    }

    @Test
    void contextLoads() {
        assertThat(compilationService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(categoryService).isNotNull();
        assertThat(eventService).isNotNull();
    }

    @Test
    public void testCompilations() {
        CompilationDto compilationDto = compilationService.createCompilation(new NewCompilationDto()
                .setEvents(List.of(eventFullDto1.getId(), eventFullDto2.getId()))
                .setPinned(true)
                .setTitle("Compilation title"));

        assertThat(compilationDto.getId()).isNotNull();
        assertEquals(2, compilationDto.getEvents().size());

        compilationDto = compilationService.getCompilationById(compilationDto.getId());

        assertThat(compilationDto.getId()).isNotNull();
        assertEquals(2, compilationDto.getEvents().size());

        assertThrows(NullPointerException.class, () -> compilationService.findCompilationById(10000));

        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest()
                .setTitle("new compilation title");
        compilationDto = compilationService.updateCompilation(compilationDto.getId(), updateCompilationRequest);

        assertEquals(compilationDto.getTitle(), updateCompilationRequest.getTitle());

        assertEquals(1, compilationService.getCompilations(true, 0, 10).size());

        compilationService.deleteCompilation(compilationDto.getId());

        CompilationDto finalCompilationDto = compilationDto;
        assertThrows(NullPointerException.class, () -> compilationService.findCompilationById(finalCompilationDto.getId()));
    }
}
