package ru.practicum.ewm.RESTTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.controller.admin.UserAdminController;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserAdminController.class})
public class RESTUserTests {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto()
            .setId(1)
            .setName("Name")
            .setEmail("email@email.email")
            .setRating(100);

    @Test
    public void testGetUsers() throws Exception {
        doReturn(List.of(userDto))
                .when(userService)
                .getUsers(any(), anyInt(), anyInt());

        mvc.perform(get("/admin/users")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.[0].rating", is(userDto.getRating()), Integer.class));

        mvc.perform(get("/admin/users?ids=1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.[0].rating", is(userDto.getRating()), Integer.class));

        mvc.perform(get("/admin/users?size=10")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.[0].rating", is(userDto.getRating()), Integer.class));

        mvc.perform(get("/admin/users?from=0")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.[0].rating", is(userDto.getRating()), Integer.class));

        mvc.perform(get("/admin/users?from=-1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());

        mvc.perform(get("/admin/users?size=-10")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreateUser() throws Exception {
        NewUserRequestDto newUserRequestDto = new NewUserRequestDto()
                .setName("")
                .setEmail("");

        doReturn(userDto)
                .when(userService)
                .createUser(any(NewUserRequestDto.class));

        mvc.perform(post("/admin/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newUserRequestDto)))
                .andExpect(status().isBadRequest());

        newUserRequestDto.setName("name");
        mvc.perform(post("/admin/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newUserRequestDto)))
                .andExpect(status().isBadRequest());

        newUserRequestDto.setEmail("email");
        mvc.perform(post("/admin/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newUserRequestDto)))
                .andExpect(status().isBadRequest());

        newUserRequestDto.setEmail("email@wmail.email");
        mvc.perform(post("/admin/users")
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(newUserRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing()
                .when(userService)
                .deleteUser(anyInt());

        mvc.perform(delete("/admin/users/")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isInternalServerError());

        mvc.perform(delete("/admin/users/1")
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isNoContent());
    }
}
