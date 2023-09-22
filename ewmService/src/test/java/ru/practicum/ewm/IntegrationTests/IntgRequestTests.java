package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.CategoryRepository;
import ru.practicum.ewm.dao.EventRepository;
import ru.practicum.ewm.dto.event.EventMapper;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgRequestTests {
    @Autowired
    private final RequestService requestService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventMapper eventMapper;
    UserDto userDto1;
    UserDto userDto2;
    Category category;
    NewEventDto newEventDto1;
    Event event1;
    NewEventDto newEventDto2;
    Event event2;

    @BeforeEach
    void setUp() {
        userDto1 = userService.createUser(
                new NewUserRequestDto()
                        .setName("User1")
                        .setEmail("email1@email1.email1"));
        userDto2 = userService.createUser(new NewUserRequestDto()
                .setName("User2")
                .setEmail("email2@email2.email2"));
        category = categoryRepository.save(new Category().setName("Category"));
        newEventDto1 = new NewEventDto()
                .setAnnotation("Annotation1")
                .setCategory(category.getId())
                .setDescription("Description1")
                .setEventDate(LocalDateTime.now().plusHours(5))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setTitle("Title1");
        event1 = eventRepository.save(eventMapper.toEvent(userDto1.getId(), newEventDto1, newEventDto1.getLocation(), category));

        eventRepository.save(event1.setState(EventState.PUBLISHED));

        newEventDto2 = new NewEventDto()
                .setAnnotation("Annotation2")
                .setCategory(category.getId())
                .setDescription("Description2")
                .setEventDate(LocalDateTime.now().plusHours(5))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setTitle("Title2");
        event2 = eventRepository.save(eventMapper.toEvent(userDto2.getId(), newEventDto2, newEventDto2.getLocation(), category));
    }

    @Test
    void contextLoads() {
        assertThat(requestService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(categoryRepository).isNotNull();
        assertThat(eventRepository).isNotNull();
    }

    @Test
    public void testRequests() {
        ParticipationRequestDto participationRequestDto1 = requestService.createRequest(userDto2.getId(), event1.getId());

        assertThrows(DataIntegrityViolationException.class, () -> requestService.createRequest(userDto2.getId(), event1.getId()));
        eventRepository.save(event2.setState(EventState.PUBLISHED));
        ParticipationRequestDto participationRequestDto2 = requestService.createRequest(userDto1.getId(), event2.getId());

        assertThat(participationRequestDto1.getId()).isNotNull();
        assertThat(participationRequestDto2.getId()).isNotNull();

        assertEquals(1, requestService.getRequestByUser(userDto1.getId()).size());

        ParticipationRequestDto cancelledRequest = requestService.cancelRequest(userDto1.getId(), participationRequestDto2.getId());
        assertEquals(RequestStatus.CANCELED, cancelledRequest.getStatus());
    }
}
