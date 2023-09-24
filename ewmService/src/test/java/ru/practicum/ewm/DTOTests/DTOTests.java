package ru.practicum.ewm.DTOTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.EventStateAdminAction;
import ru.practicum.ewm.model.EventStateUserAction;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.statistics.client.DateTimeFormatterUtility;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DTOTests {
    @Autowired
    private JacksonTester<CategoryDto> jsonCategoryDto;
    @Autowired
    private JacksonTester<NewCategoryDto> jsonNewCategoryDto;
    @Autowired
    private JacksonTester<CompilationDto> jsonCompilationDto;
    @Autowired
    private JacksonTester<NewCompilationDto> jsonNewCompilationDto;
    @Autowired
    private JacksonTester<UpdateCompilationRequest> jsonUpdateCompilationRequest;
    @Autowired
    private JacksonTester<EventFullDto> jsonEventFullDto;
    @Autowired
    private JacksonTester<EventShortDto> jsonEventShortDto;
    @Autowired
    private JacksonTester<NewEventDto> jsonNewEventDto;
    @Autowired
    private JacksonTester<UpdateEventAdminRequestDto> jsonUpdateEventAdminRequestDto;
    @Autowired
    JacksonTester<UpdateEventUserRequestDto> jsonUpdateEventUserRequestDto;
    @Autowired
    JacksonTester<EventRequestStatusUpdateRequestDto> jsonEventRequestStatusUpdateRequestDto;
    @Autowired
    JacksonTester<EventRequestStatusUpdateResultDto> jsonEventRequestStatusUpdateResultDto;
    @Autowired
    JacksonTester<ParticipationRequestDto> jsonParticipationRequestDto;
    @Autowired
    JacksonTester<NewUserRequestDto> jsonNewUserRequestDto;
    @Autowired
    JacksonTester<UserDto> jsonUserDto;
    @Autowired
    JacksonTester<UserShortDto> jsonUserShortDto;

    @Test
    public void testCategoryDto() throws IOException {
        CategoryDto categoryDto = new CategoryDto()
                .setId(1)
                .setName("Name");

        JsonContent<CategoryDto> result = jsonCategoryDto.write(categoryDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(categoryDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(categoryDto.getName());
    }

    @Test
    public void testNewCategoryDto() throws IOException {
        NewCategoryDto newCategoryDto = new NewCategoryDto()
                .setName("name");

        JsonContent<NewCategoryDto> result = jsonNewCategoryDto.write(newCategoryDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(newCategoryDto.getName());
    }

    @Test
    public void testCompilationDto() throws IOException {
        CompilationDto compilationDto = new CompilationDto()
                .setEvents(List.of(new EventShortDto()
                        .setAnnotation("Annotation")
                        .setCategory(new CategoryDto()
                                .setId(1)
                                .setName("CategoryName"))
                        .setConfirmedRequests(1)
                        .setEventDate(LocalDateTime.now())
                        .setId(1)
                        .setInitiator(new UserShortDto()
                                .setId(1)
                                .setName("InitiatorName")
                                .setRating(100))
                        .setPaid(true)
                        .setTitle("EventTitle")
                        .setViews(1000)
                        .setRating(99)))
                .setId(1)
                .setPinned(true)
                .setTitle("Title");

        JsonContent<CompilationDto> result = jsonCompilationDto.write(compilationDto);

        assertThat(result).extractingJsonPathStringValue("$.events.[0].annotation").isEqualTo(compilationDto.getEvents().get(0).getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].category.id").isEqualTo(compilationDto.getEvents().get(0).getCategory().getId());
        assertThat(result).extractingJsonPathStringValue("$.events.[0].category.name").isEqualTo(compilationDto.getEvents().get(0).getCategory().getName());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].confirmedRequests").isEqualTo(compilationDto.getEvents().get(0).getConfirmedRequests());
        assertThat(result).extractingJsonPathStringValue("$.events.[0].eventDate").isEqualTo(compilationDto.getEvents().get(0).getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].id").isEqualTo(compilationDto.getEvents().get(0).getId());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].initiator.id").isEqualTo(compilationDto.getEvents().get(0).getInitiator().getId());
        assertThat(result).extractingJsonPathStringValue("$.events.[0].initiator.name").isEqualTo(compilationDto.getEvents().get(0).getInitiator().getName());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].initiator.rating").isEqualTo(compilationDto.getEvents().get(0).getInitiator().getRating());
        assertThat(result).extractingJsonPathBooleanValue("$.events.[0].paid").isEqualTo(compilationDto.getEvents().get(0).getPaid());
        assertThat(result).extractingJsonPathStringValue("$.events.[0].title").isEqualTo(compilationDto.getEvents().get(0).getTitle());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].views").isEqualTo(compilationDto.getEvents().get(0).getViews());
        assertThat(result).extractingJsonPathNumberValue("$.events.[0].rating").isEqualTo(compilationDto.getEvents().get(0).getRating());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(compilationDto.getId());
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(compilationDto.getPinned());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(compilationDto.getTitle());
    }

    @Test
    public void testNewCompilationDto() throws IOException {
        NewCompilationDto newCompilationDto = new NewCompilationDto()
                .setEvents(List.of(1, 2, 3))
                .setPinned(true)
                .setTitle("Title");

        JsonContent<NewCompilationDto> result = jsonNewCompilationDto.write(newCompilationDto);

        assertThat(result).extractingJsonPathNumberValue("$.events.[0]").isEqualTo(newCompilationDto.getEvents().get(0));
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(newCompilationDto.getPinned());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(newCompilationDto.getTitle());
    }

    @Test
    public void testUpdateCompilationRequest() throws IOException {
        UpdateCompilationRequest updateCompilationRequest = new UpdateCompilationRequest()
                .setEvents(List.of(1, 2, 3))
                .setPinned(true)
                .setTitle("Title");

        JsonContent<UpdateCompilationRequest> result = jsonUpdateCompilationRequest.write(updateCompilationRequest);

        assertThat(result).extractingJsonPathNumberValue("$.events.[0]").isEqualTo(updateCompilationRequest.getEvents().get(0));
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(updateCompilationRequest.getPinned());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(updateCompilationRequest.getTitle());
    }

    @Test
    public void testEventFullDto() throws IOException {
        EventFullDto eventFullDto = new EventFullDto()
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

        JsonContent<EventFullDto> result = jsonEventFullDto.write(eventFullDto);

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(eventFullDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category.id").isEqualTo(eventFullDto.getCategory().getId());
        assertThat(result).extractingJsonPathStringValue("$.category.name").isEqualTo(eventFullDto.getCategory().getName());
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(eventFullDto.getConfirmedRequests());
        assertThat(result).extractingJsonPathStringValue("$.createdOn").isEqualTo(eventFullDto.getCreatedOn().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(eventFullDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(eventFullDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(eventFullDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(eventFullDto.getInitiator().getId());
        assertThat(result).extractingJsonPathStringValue("$.initiator.name").isEqualTo(eventFullDto.getInitiator().getName());
        assertThat(result).extractingJsonPathNumberValue("$.initiator.rating").isEqualTo(eventFullDto.getInitiator().getRating());
        assertThat(result).extractingJsonPathNumberValue("$.location.lat").isEqualTo((double) Math.round(eventFullDto.getLocation().getLat().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathNumberValue("$.location.lon").isEqualTo((double) Math.round(eventFullDto.getLocation().getLon().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(eventFullDto.getPaid());
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(eventFullDto.getParticipantLimit());
        assertThat(result).extractingJsonPathStringValue("$.publishedOn").isEqualTo(eventFullDto.getPublishedOn().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(eventFullDto.getRequestModeration());
        assertThat(result).extractingJsonPathStringValue("$.state").isEqualTo(eventFullDto.getState().name());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(eventFullDto.getTitle());
        assertThat(result).extractingJsonPathNumberValue("$.views").isEqualTo(eventFullDto.getViews());
        assertThat(result).extractingJsonPathNumberValue("$.rating").isEqualTo(eventFullDto.getRating());
    }

    @Test
    public void testEventShortDto() throws IOException {
        EventShortDto eventShortDto = new EventShortDto()
                .setAnnotation("Annotation")
                .setCategory(new CategoryDto()
                        .setId(1)
                        .setName("CategoryName"))
                .setConfirmedRequests(1)
                .setEventDate(LocalDateTime.now())
                .setId(1)
                .setInitiator(new UserShortDto()
                        .setId(1)
                        .setName("InitiatorName")
                        .setRating(100))
                .setPaid(true)
                .setTitle("EventTitle")
                .setViews(1000)
                .setRating(99);

        JsonContent<EventShortDto> result = jsonEventShortDto.write(eventShortDto);

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(eventShortDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category.id").isEqualTo(eventShortDto.getCategory().getId());
        assertThat(result).extractingJsonPathStringValue("$.category.name").isEqualTo(eventShortDto.getCategory().getName());
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(eventShortDto.getConfirmedRequests());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(eventShortDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(eventShortDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.initiator.id").isEqualTo(eventShortDto.getInitiator().getId());
        assertThat(result).extractingJsonPathStringValue("$.initiator.name").isEqualTo(eventShortDto.getInitiator().getName());
        assertThat(result).extractingJsonPathNumberValue("$.initiator.rating").isEqualTo(eventShortDto.getInitiator().getRating());
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(eventShortDto.getPaid());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(eventShortDto.getTitle());
        assertThat(result).extractingJsonPathNumberValue("$.views").isEqualTo(eventShortDto.getViews());
        assertThat(result).extractingJsonPathNumberValue("$.rating").isEqualTo(eventShortDto.getRating());
    }

    @Test
    public void testNewEventDto() throws IOException {
        NewEventDto newEventDto = new NewEventDto()
                .setAnnotation("Annotation")
                .setCategory(10)
                .setDescription("Description")
                .setEventDate(LocalDateTime.now())
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setTitle("Title");

        JsonContent<NewEventDto> result = jsonNewEventDto.write(newEventDto);

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(newEventDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category").isEqualTo(newEventDto.getCategory());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(newEventDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(newEventDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.location.lat").isEqualTo((double) Math.round(newEventDto.getLocation().getLat().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathNumberValue("$.location.lon").isEqualTo((double) Math.round(newEventDto.getLocation().getLon().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(newEventDto.getPaid());
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(newEventDto.getParticipantLimit());
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(newEventDto.getRequestModeration());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(newEventDto.getTitle());
    }

    @Test
    public void testUpdateEventAdminRequestDto() throws IOException {
        UpdateEventAdminRequestDto updateEventAdminRequestDto = new UpdateEventAdminRequestDto()
                .setAnnotation("Annotation")
                .setCategory(10)
                .setDescription("Description")
                .setEventDate(LocalDateTime.now())
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setStateAction(EventStateAdminAction.PUBLISH_EVENT)
                .setTitle("Title");

        JsonContent<UpdateEventAdminRequestDto> result = jsonUpdateEventAdminRequestDto.write(updateEventAdminRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(updateEventAdminRequestDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category").isEqualTo(updateEventAdminRequestDto.getCategory());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(updateEventAdminRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(updateEventAdminRequestDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.location.lat").isEqualTo((double) Math.round(updateEventAdminRequestDto.getLocation().getLat().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathNumberValue("$.location.lon").isEqualTo((double) Math.round(updateEventAdminRequestDto.getLocation().getLon().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(updateEventAdminRequestDto.getPaid());
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(updateEventAdminRequestDto.getParticipantLimit());
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(updateEventAdminRequestDto.getRequestModeration());
        assertThat(result).extractingJsonPathStringValue("$.stateAction").isEqualTo(updateEventAdminRequestDto.getStateAction().name());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(updateEventAdminRequestDto.getTitle());
    }

    @Test
    public void testUpdateEventUserRequestDto() throws IOException {
        UpdateEventUserRequestDto updateEventUserRequestDto = new UpdateEventUserRequestDto()
                .setAnnotation("Annotation")
                .setCategory(10)
                .setDescription("Description")
                .setEventDate(LocalDateTime.now())
                .setLocation(new Location()
                        .setLat(11.11F)
                        .setLon(11.11F))
                .setPaid(true)
                .setParticipantLimit(10)
                .setRequestModeration(true)
                .setStateAction(EventStateUserAction.SEND_TO_REVIEW)
                .setTitle("Title");

        JsonContent<UpdateEventUserRequestDto> result = jsonUpdateEventUserRequestDto.write(updateEventUserRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo(updateEventUserRequestDto.getAnnotation());
        assertThat(result).extractingJsonPathNumberValue("$.category").isEqualTo(updateEventUserRequestDto.getCategory());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(updateEventUserRequestDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo(updateEventUserRequestDto.getEventDate().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.location.lat").isEqualTo((double) Math.round(updateEventUserRequestDto.getLocation().getLat().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathNumberValue("$.location.lon").isEqualTo((double) Math.round(updateEventUserRequestDto.getLocation().getLon().doubleValue() * 100) / 100);
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isEqualTo(updateEventUserRequestDto.getPaid());
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(updateEventUserRequestDto.getParticipantLimit());
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isEqualTo(updateEventUserRequestDto.getRequestModeration());
        assertThat(result).extractingJsonPathStringValue("$.stateAction").isEqualTo(updateEventUserRequestDto.getStateAction().name());
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo(updateEventUserRequestDto.getTitle());
    }

    @Test
    public void testEventRequestStatusUpdateRequestDto() throws IOException {
        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto = new EventRequestStatusUpdateRequestDto()
                .setRequestIds(List.of(1, 2))
                .setStatus(RequestStatus.CONFIRMED);

        JsonContent<EventRequestStatusUpdateRequestDto> result = jsonEventRequestStatusUpdateRequestDto.write(eventRequestStatusUpdateRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.requestIds.[0]").isEqualTo(eventRequestStatusUpdateRequestDto.getRequestIds().get(0));
        assertThat(result).extractingJsonPathNumberValue("$.requestIds.[1]").isEqualTo(eventRequestStatusUpdateRequestDto.getRequestIds().get(1));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(eventRequestStatusUpdateRequestDto.getStatus().name());
    }

    @Test
    public void testEventRequestStatusUpdateResultDto() throws IOException {
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
                        .setStatus(RequestStatus.REJECTED)));

        JsonContent<EventRequestStatusUpdateResultDto> result = jsonEventRequestStatusUpdateResultDto.write(eventRequestStatusUpdateResultDto);

        assertThat(result).extractingJsonPathStringValue("$.confirmedRequests.[0].created").isEqualTo(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getCreated().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests.[0].event").isEqualTo(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getEvent());
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests.[0].id").isEqualTo(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getId());
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests.[0].requester").isEqualTo(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getRequester());
        assertThat(result).extractingJsonPathStringValue("$.confirmedRequests.[0].status").isEqualTo(eventRequestStatusUpdateResultDto.getConfirmedRequests().get(0).getStatus().name());

        assertThat(result).extractingJsonPathStringValue("$.rejectedRequests.[0].created").isEqualTo(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getCreated().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.rejectedRequests.[0].event").isEqualTo(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getEvent());
        assertThat(result).extractingJsonPathNumberValue("$.rejectedRequests.[0].id").isEqualTo(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getId());
        assertThat(result).extractingJsonPathNumberValue("$.rejectedRequests.[0].requester").isEqualTo(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getRequester());
        assertThat(result).extractingJsonPathStringValue("$.rejectedRequests.[0].status").isEqualTo(eventRequestStatusUpdateResultDto.getRejectedRequests().get(0).getStatus().name());
    }

    @Test
    public void testParticipationRequestDto() throws IOException {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto()
                .setCreated(LocalDateTime.now())
                .setEvent(1)
                .setId(1)
                .setRequester(1)
                .setStatus(RequestStatus.CONFIRMED);

        JsonContent<ParticipationRequestDto> result = jsonParticipationRequestDto.write(participationRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(participationRequestDto.getCreated().format(DateTimeFormatterUtility.FORMATTER));
        assertThat(result).extractingJsonPathNumberValue("$.event").isEqualTo(participationRequestDto.getEvent());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(participationRequestDto.getId());
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(participationRequestDto.getRequester());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(participationRequestDto.getStatus().name());
    }

    @Test
    public void testNewUserRequestDto() throws IOException {
        NewUserRequestDto newUserRequestDto = new NewUserRequestDto()
                .setEmail("Email")
                .setName("Name");

        JsonContent<NewUserRequestDto> result = jsonNewUserRequestDto.write(newUserRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(newUserRequestDto.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(newUserRequestDto.getName());
    }

    @Test
    public void testUserDto() throws IOException {
        UserDto userDto = new UserDto()
                .setEmail("Email")
                .setId(1)
                .setName("Name")
                .setRating(99);

        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.rating").isEqualTo(userDto.getRating());
    }

    @Test
    public void testUserShortDto() throws IOException {
        UserShortDto userShortDto = new UserShortDto()
                .setId(1)
                .setName("Name")
                .setRating(99);

        JsonContent<UserShortDto> result = jsonUserShortDto.write(userShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(userShortDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userShortDto.getName());
        assertThat(result).extractingJsonPathNumberValue("$.rating").isEqualTo(userShortDto.getRating());
    }
}
