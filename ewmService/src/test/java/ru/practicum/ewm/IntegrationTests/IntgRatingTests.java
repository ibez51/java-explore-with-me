package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.Location;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.model.EventStateAdminAction;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.service.*;
import ru.practicum.ewm.statistics.client.ServiceHTTPClient;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgRatingTests {
    @Autowired
    private final EventService eventService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final RequestService requestService;
    @Autowired
    private final RatingService ratingService;
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
        assertThat(ratingService).isNotNull();
    }

    @Test
    public void testRatings() {
        assertThrows(DataIntegrityViolationException.class, () -> eventService.rateEvent(userDto2.getId(), eventFullDto1.getId(), true));

        eventService.updateEvent(eventFullDto1.getId(), new UpdateEventAdminRequestDto().setStateAction(EventStateAdminAction.PUBLISH_EVENT));

        UserDto userDto3 = userService.createUser(
                new NewUserRequestDto()
                        .setName("User3")
                        .setEmail("email3@email1.email1"));
        UserDto userDto4 = userService.createUser(
                new NewUserRequestDto()
                        .setName("User4")
                        .setEmail("email4@email1.email1"));

        ParticipationRequestDto participationRequestDto2 = requestService.createRequest(userDto2.getId(), eventFullDto1.getId());
        ParticipationRequestDto participationRequestDto3 = requestService.createRequest(userDto3.getId(), eventFullDto1.getId());
        ParticipationRequestDto participationRequestDto4 = requestService.createRequest(userDto4.getId(), eventFullDto1.getId());

        assertThrows(DataIntegrityViolationException.class, () -> eventService.rateEvent(userDto2.getId(), eventFullDto1.getId(), true));

        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto = new EventRequestStatusUpdateRequestDto()
                .setRequestIds(List.of(participationRequestDto2.getId(),
                        participationRequestDto3.getId(),
                        participationRequestDto4.getId()))
                .setStatus(RequestStatus.CONFIRMED);
        eventService.updateRequestStatus(userDto1.getId(), eventFullDto1.getId(), eventRequestStatusUpdateRequestDto);

        assertThrows(DataIntegrityViolationException.class, () -> eventService.rateEvent(userDto2.getId(), eventFullDto1.getId(), true));

        eventService.updateEventDateForce(eventFullDto1.getId(), LocalDateTime.now().minusHours(10));

        EventFullDto eventFullDtoRated = eventService.rateEvent(userDto2.getId(), eventFullDto1.getId(), true);

        assertThat(eventFullDtoRated).isNotNull();
        assertEquals(100, eventFullDtoRated.getRating());

        eventFullDtoRated = eventService.deleteRate(userDto2.getId(), eventFullDto1.getId());

        assertEquals(0, eventFullDtoRated.getRating());

        eventService.rateEvent(userDto2.getId(), eventFullDto1.getId(), true);
        eventService.rateEvent(userDto3.getId(), eventFullDto1.getId(), true);
        eventFullDtoRated = eventService.rateEvent(userDto4.getId(), eventFullDto1.getId(), true);

        assertEquals(100, eventFullDtoRated.getRating());

        eventFullDtoRated = eventService.rateEvent(userDto4.getId(), eventFullDto1.getId(), false);

        assertEquals(66, eventFullDtoRated.getRating());

        userDto1 = userService.getUsers(List.of(userDto1.getId()), 0, 10).get(0);
        assertEquals(66, userDto1.getRating());
    }
}
