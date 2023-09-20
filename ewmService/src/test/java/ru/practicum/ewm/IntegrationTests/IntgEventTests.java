package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.exceptions.InvalidParameterException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.statistics.client.ServiceHTTPClient;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgEventTests {
    @Autowired
    private final EventService eventService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final RequestService requestService;
    @MockBean
    private final ServiceHTTPClient serviceHTTPClient;
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
        eventFullDto2 = eventService.createEvent(userDto2.getId(),
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

        ResponseEntity<List<UriCalledStatisticDto>> response;
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(HttpStatus.OK);

        UriCalledStatisticDto uriCalledStatisticDto1 = new UriCalledStatisticDto();
        uriCalledStatisticDto1.setUri("/events/" + eventFullDto1.getId());
        uriCalledStatisticDto1.setHits(10);

        UriCalledStatisticDto uriCalledStatisticDto2 = new UriCalledStatisticDto();
        uriCalledStatisticDto2.setUri("/events/" + eventFullDto2.getId());
        uriCalledStatisticDto2.setHits(10);

        response = responseBuilder.body(List.of(uriCalledStatisticDto1, uriCalledStatisticDto2));

        doReturn(response)
                .when(serviceHTTPClient)
                .getStatistics(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean());

        doReturn(null)
                .when(serviceHTTPClient)
                .postHitUri(any(UriCalledDto.class));
    }

    @Test
    void contextLoads() {
        assertThat(userService).isNotNull();
        assertThat(categoryService).isNotNull();
        assertThat(eventService).isNotNull();
        assertThat(requestService).isNotNull();
    }

    @Test
    public void testEvents() {
        assertThat(eventFullDto1.getId()).isNotNull();

        assertEquals(1, eventService.getAllEventsByInitiator(userDto1.getId(), 0, 10).size());

        assertEquals(1, eventService.getAllEventsByInitiator(userDto2.getId(), 0, 10).size());

        assertThat(eventService.getEventByUserAndId(userDto1.getId(), eventFullDto1.getId())).isNotNull();

        UpdateEventUserRequestDto updateEventUserRequestDto = new UpdateEventUserRequestDto()
                .setTitle("New title")
                .setPaid(false);
        eventFullDto1 = eventService.updateEvent(userDto1.getId(), eventFullDto1.getId(), updateEventUserRequestDto);
        assertEquals(updateEventUserRequestDto.getTitle(), eventFullDto1.getTitle());
        assertEquals(updateEventUserRequestDto.getPaid(), eventFullDto1.getPaid());

        eventService.updateEvent(eventFullDto1.getId(), new UpdateEventAdminRequestDto().setStateAction(EventStateAdminAction.PUBLISH_EVENT));
        ParticipationRequestDto participationRequestDto1 = requestService.createRequest(userDto2.getId(), eventFullDto1.getId());
        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto = new EventRequestStatusUpdateRequestDto()
                .setRequestIds(List.of(participationRequestDto1.getId()))
                .setStatus(RequestStatus.CONFIRMED);
        EventRequestStatusUpdateResultDto eventRequestStatusUpdateResultDto = eventService.updateRequestStatus(userDto1.getId(), eventFullDto1.getId(), eventRequestStatusUpdateRequestDto);
        assertThat(eventRequestStatusUpdateResultDto.getConfirmedRequests()).isNotNull();

        assertEquals(1, eventService.getAllParticipationRequestForEvent(userDto1.getId(), eventFullDto1.getId()).size());

        assertEquals(2, eventService.findEvents(List.of(userDto1.getId(), userDto2.getId()),
                List.of(EventState.PUBLISHED, EventState.CANCELED, EventState.PENDING),
                List.of(categoryDto.getId()),
                LocalDateTime.now().minusYears(20),
                LocalDateTime.now().plusYears(20),
                0,
                10).size());

        assertEquals(1, eventService.getAllEvent("A",
                List.of(categoryDto.getId()),
                false,
                LocalDateTime.now().minusYears(20),
                LocalDateTime.now().plusYears(20),
                false,
                EventSortType.EVENT_DATE,
                0,
                10,
                mock(HttpServletRequest.class)).size());
        assertThrows(InvalidParameterException.class, () -> eventService.getAllEvent("A",
                List.of(categoryDto.getId()),
                false,
                LocalDateTime.now().plusYears(20),
                LocalDateTime.now().minusYears(20),
                false,
                EventSortType.EVENT_DATE,
                0,
                10,
                mock(HttpServletRequest.class)));

        assertThat(eventService.getPublicEventById(eventFullDto1.getId(), mock(HttpServletRequest.class))).isNotNull();

        Event event = eventService.findEventById(eventFullDto1.getId());
        assertThat(eventService.findEventByIdAndState(eventFullDto1.getId(), event.getState())).isNotNull();
    }
}
