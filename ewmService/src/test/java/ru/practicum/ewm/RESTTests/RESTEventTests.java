package ru.practicum.ewm.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.admin.EventAdminController;
import ru.practicum.ewm.controller.priv.EventPrivateController;
import ru.practicum.ewm.controller.pub.EventPublicController;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EventAdminController.class, EventPrivateController.class, EventPublicController.class})
public class RESTEventTests {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    EventService eventService;
    @Autowired
    private MockMvc mvc;

    private final EventFullDto eventFullDto = new EventFullDto()
            .setAnnotation("Annotation")
            .setCategory(new CategoryDto()
                    .setId(1)
                    .setName("CategoryName"))
            .setConfirmedRequests(5)
            .setCreatedOn(LocalDateTime.now())
            .setDescription("Description")
            .setEventDate(LocalDateTime.now())
            .setId(1)
            .setInitiator(new UserShortDto()
                    .setId(1)
                    .setName("UserName")
                    .setRating(100))
            .setLocation(new Location()
                    .setLat(11.11F)
                    .setLon(22.22F))
            .setPaid(true)
            .setParticipantLimit(10)
            .setPublishedOn(LocalDateTime.now())
            .setRequestModeration(true)
            .setState(EventState.PUBLISHED)
            .setTitle("Title")
            .setViews(5)
            .setRating(99);

    private final EventShortDto eventShortDto = new EventShortDto()
            .setAnnotation("Annotation")
            .setCategory(new CategoryDto()
                    .setId(1)
                    .setName("CategoryName"))
            .setConfirmedRequests(10)
            .setEventDate(LocalDateTime.now())
            .setId(1)
            .setInitiator(new UserShortDto()
                    .setId(1)
                    .setName("Name")
                    .setRating(100))
            .setPaid(true)
            .setTitle("Title")
            .setViews(10)
            .setRating(99);

    @Test
    public void testFindEvents() throws Exception {
        doReturn(List.of(eventFullDto))
                .when(eventService)
                .findEvents(any(), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class), any(Integer.class), any(Integer.class));

        mvc.perform(get("/admin/events")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());

        mvc.perform(get("/admin/events?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/admin/events?size=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .updateEvent(anyInt(), any(UpdateEventAdminRequestDto.class));

        UpdateEventAdminRequestDto updateEventAdminRequestDto = new UpdateEventAdminRequestDto()
                .setAnnotation("A".repeat(30))
                .setCategory(1)
                .setDescription("D".repeat(100))
                .setEventDate(LocalDateTime.now().plusHours(1))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setStateAction(EventStateAdminAction.PUBLISH_EVENT)
                .setTitle("Title");

        mvc.perform(patch("/admin/events/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateEventAdminRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation())))
                .andExpect(jsonPath("$.category.id", is(eventFullDto.getCategory().getId()), Integer.class))
                .andExpect(jsonPath("$.category.name", is(eventFullDto.getCategory().getName())))
                .andExpect(jsonPath("$.confirmedRequests", is(eventFullDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.createdOn", is(eventFullDto.getCreatedOn().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(eventFullDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.initiator.id", is(eventFullDto.getInitiator().getId()), Integer.class))
                .andExpect(jsonPath("$.initiator.name", is(eventFullDto.getInitiator().getName())))
                .andExpect(jsonPath("$.initiator.rating", is(eventFullDto.getInitiator().getRating()), Integer.class))
                .andExpect(jsonPath("$.paid", is(eventFullDto.getPaid())))
                .andExpect(jsonPath("$.participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$.publishedOn", is(eventFullDto.getPublishedOn().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.requestModeration", is(eventFullDto.getRequestModeration())))
                .andExpect(jsonPath("$.state", is(eventFullDto.getState().name())))
                .andExpect(jsonPath("$.title", is(eventFullDto.getTitle())))
                .andExpect(jsonPath("$.views", is(eventFullDto.getViews()), Integer.class))
                .andExpect(jsonPath("$.rating", is(eventFullDto.getRating()), Integer.class));
    }

    @Test
    public void testGetAllEventsByInitiator() throws Exception {
        doReturn(List.of(eventShortDto))
                .when(eventService)
                .getAllEventsByInitiator(anyInt(), anyInt(), anyInt());

        mvc.perform(get("/users/1/events")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].annotation", is(eventShortDto.getAnnotation())))
                .andExpect(jsonPath("$.[0].category.id", is(eventShortDto.getCategory().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].category.name", is(eventShortDto.getCategory().getName())))
                .andExpect(jsonPath("$.[0].confirmedRequests", is(eventShortDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.[0].eventDate", is(eventShortDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.[0].id", is(eventShortDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].initiator.id", is(eventShortDto.getInitiator().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].initiator.name", is(eventShortDto.getInitiator().getName())))
                .andExpect(jsonPath("$.[0].initiator.rating", is(eventShortDto.getInitiator().getRating()), Integer.class))
                .andExpect(jsonPath("$.[0].paid", is(eventShortDto.getPaid())))
                .andExpect(jsonPath("$.[0].title", is(eventShortDto.getTitle())))
                .andExpect(jsonPath("$.[0].views", is(eventShortDto.getViews()), Integer.class))
                .andExpect(jsonPath("$.[0].rating", is(eventShortDto.getRating()), Integer.class));

        mvc.perform(get("/users/1/events?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/users/1/events?size=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateEvent() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .createEvent(anyInt(), any(NewEventDto.class));

        NewEventDto newEventDto = new NewEventDto()
                .setAnnotation("A".repeat(20))
                .setCategory(1)
                .setDescription("D".repeat(20))
                .setEventDate(LocalDateTime.now().plusHours(1))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setTitle("Title");

        mvc.perform(post("/users/1/events")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation())))
                .andExpect(jsonPath("$.category.id", is(eventFullDto.getCategory().getId()), Integer.class))
                .andExpect(jsonPath("$.category.name", is(eventFullDto.getCategory().getName())))
                .andExpect(jsonPath("$.confirmedRequests", is(eventFullDto.getConfirmedRequests()), Integer.class))
                .andExpect(jsonPath("$.createdOn", is(eventFullDto.getCreatedOn().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.description", is(eventFullDto.getDescription())))
                .andExpect(jsonPath("$.eventDate", is(eventFullDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.id", is(eventFullDto.getId()), Integer.class))
                .andExpect(jsonPath("$.initiator.id", is(eventFullDto.getInitiator().getId()), Integer.class))
                .andExpect(jsonPath("$.initiator.name", is(eventFullDto.getInitiator().getName())))
                .andExpect(jsonPath("$.initiator.rating", is(eventFullDto.getInitiator().getRating()), Integer.class))
                .andExpect(jsonPath("$.paid", is(eventFullDto.getPaid())))
                .andExpect(jsonPath("$.participantLimit", is(eventFullDto.getParticipantLimit()), Integer.class))
                .andExpect(jsonPath("$.publishedOn", is(eventFullDto.getPublishedOn().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.requestModeration", is(eventFullDto.getRequestModeration())))
                .andExpect(jsonPath("$.state", is(eventFullDto.getState().name())))
                .andExpect(jsonPath("$.title", is(eventFullDto.getTitle())))
                .andExpect(jsonPath("$.views", is(eventFullDto.getViews()), Integer.class))
                .andExpect(jsonPath("$.rating", is(eventFullDto.getRating()), Integer.class));

        newEventDto.setAnnotation("1");
        mvc.perform(post("/users/1/events")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newEventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEventByUserAndId() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .getEventByUserAndId(anyInt(), anyInt());

        mvc.perform(get("/users/1/events/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation())));
    }

    @Test
    public void testUpdateEventUser() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .updateEvent(anyInt(), anyInt(), any(UpdateEventUserRequestDto.class));

        UpdateEventUserRequestDto updateEventUserRequestDto = new UpdateEventUserRequestDto()
                .setAnnotation("A".repeat(20))
                .setCategory(1)
                .setDescription("D".repeat(20))
                .setEventDate(LocalDateTime.now().plusHours(1))
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setStateAction(EventStateUserAction.SEND_TO_REVIEW)
                .setTitle("Title");

        mvc.perform(patch("/users/1/events/1")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(updateEventUserRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annotation", is(eventFullDto.getAnnotation())));
    }

    @Test
    public void testGetAllParticipationRequestForEvent() throws Exception {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto()
                .setCreated(LocalDateTime.now())
                .setEvent(1)
                .setId(1)
                .setRequester(1)
                .setStatus(RequestStatus.CONFIRMED);

        doReturn(List.of(participationRequestDto))
                .when(eventService)
                .getAllParticipationRequestForEvent(anyInt(), anyInt());

        mvc.perform(get("/users/1/events/1/requests")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].created", is(participationRequestDto.getCreated().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.[0].event", is(participationRequestDto.getEvent()), Integer.class))
                .andExpect(jsonPath("$.[0].id", is(participationRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester", is(participationRequestDto.getRequester()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(participationRequestDto.getStatus().name())));
    }

    @Test
    public void testUpdateRequestStatus() throws Exception {
        EventRequestStatusUpdateResultDto eventRequestStatusUpdateResultDto = new EventRequestStatusUpdateResultDto()
                .setConfirmedRequests(List.of(new ParticipationRequestDto()
                        .setCreated(LocalDateTime.now())
                        .setEvent(1)
                        .setId(1)
                        .setRequester(1)
                        .setStatus(RequestStatus.CONFIRMED)))
                .setRejectedRequests(List.of(new ParticipationRequestDto()
                        .setCreated(LocalDateTime.now())
                        .setEvent(1)
                        .setId(1)
                        .setRequester(1)
                        .setStatus(RequestStatus.CONFIRMED)));

        doReturn(eventRequestStatusUpdateResultDto)
                .when(eventService)
                .updateRequestStatus(anyInt(), anyInt(), any(EventRequestStatusUpdateRequestDto.class));

        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto = new EventRequestStatusUpdateRequestDto()
                .setRequestIds(List.of(1, 2))
                .setStatus(RequestStatus.CONFIRMED);

        mvc.perform(patch("/users/1/events/1/requests")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(eventRequestStatusUpdateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests.[0].created", is(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getCreated().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.confirmedRequests.[0].event", is(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getEvent()), Integer.class))
                .andExpect(jsonPath("$.confirmedRequests.[0].id", is(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$.confirmedRequests.[0].requester", is(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getRequester()), Integer.class))
                .andExpect(jsonPath("$.confirmedRequests.[0].status", is(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getStatus().name())))
                .andExpect(jsonPath("$.rejectedRequests.[0].created", is(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getCreated().format(DateTimeFormatterUtility.FORMATTER))))
                .andExpect(jsonPath("$.rejectedRequests.[0].event", is(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getEvent()), Integer.class))
                .andExpect(jsonPath("$.rejectedRequests.[0].id", is(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getId()), Integer.class))
                .andExpect(jsonPath("$.rejectedRequests.[0].requester", is(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getRequester()), Integer.class))
                .andExpect(jsonPath("$.rejectedRequests.[0].status", is(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getStatus().name())));
    }

    @Test
    public void testRateEvent() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .rateEvent(anyInt(), anyInt(), anyBoolean());

        mvc.perform(post("/users/1/events/1/rate?isGood=false")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating", is(eventFullDto.getRating())));

        mvc.perform(post("/users/1/events/1/rate")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testReRateEvent() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .rateEvent(anyInt(), anyInt(), anyBoolean());

        mvc.perform(patch("/users/1/events/1/rate?isGood=false")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(eventFullDto.getRating())));

        mvc.perform(patch("/users/1/events/1/rate")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRemoveRate() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .deleteRate(anyInt(), anyInt());

        mvc.perform(delete("/users/1/events/1/rate")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating", is(eventFullDto.getRating())));
    }

    @Test
    public void testGetAllEventsPublic() throws Exception {
        doReturn(List.of(eventShortDto))
                .when(eventService)
                .getAllEvent(anyString(), any(), anyBoolean(), any(LocalDateTime.class), any(LocalDateTime.class), anyBoolean(), any(EventSortType.class), anyInt(), anyInt(), any(HttpServletRequest.class));

        mvc.perform(get("/events")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());

        mvc.perform(get("/events?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/events?size=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAllEvents() throws Exception {
        doReturn(eventFullDto)
                .when(eventService)
                .getPublicEventById(anyInt(), any(HttpServletRequest.class));

        mvc.perform(get("/events/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }
}
