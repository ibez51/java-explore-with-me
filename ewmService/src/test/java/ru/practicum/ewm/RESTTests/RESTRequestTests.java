package ru.practicum.ewm.RESTTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.priv.RequestPrivateController;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RequestPrivateController.class})
public class RESTRequestTests {
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mvc;
    private final ParticipationRequestDto participationRequestDto = new ParticipationRequestDto()
            .setCreated(LocalDateTime.now())
            .setEvent(1)
            .setId(1)
            .setRequester(1)
            .setStatus(RequestStatus.CONFIRMED);

    @Test
    public void testGetRequestByUser() throws Exception {
        doReturn(List.of(participationRequestDto))
                .when(requestService)
                .getRequestByUser(anyInt());

        mvc.perform(get("/users/1/requests")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].created", is(participationRequestDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))))
                .andExpect(jsonPath("$.[0].event", is(participationRequestDto.getEvent()), Integer.class))
                .andExpect(jsonPath("$.[0].id", is(participationRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester", is(participationRequestDto.getRequester()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(participationRequestDto.getStatus().name())));
    }

    @Test
    public void testCreateRequest() throws Exception {
        doReturn(participationRequestDto)
                .when(requestService)
                .createRequest(anyInt(), anyInt());

        mvc.perform(post("/users/1/requests?eventId=1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCancelRequest() throws Exception {
        doReturn(participationRequestDto)
                .when(requestService)
                .cancelRequest(anyInt(), anyInt());

        mvc.perform(patch("/users/1/requests/1/cancel")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk());
    }
}
