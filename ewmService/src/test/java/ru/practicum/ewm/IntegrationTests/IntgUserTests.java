package ru.practicum.ewm.IntegrationTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class IntgUserTests {
    @Autowired
    private final UserService userService;

    @Test
    void contextLoads() {
        assertThat(userService).isNotNull();
    }

    @Test
    public void testUserService() {
        NewUserRequestDto newUserRequestDto = new NewUserRequestDto()
                .setName("User name")
                .setEmail("user2email.user");

        UserDto userDto = userService.createUser(newUserRequestDto);
        assertThat(userDto.getId()).isNotNull();

        User user = userService.findUserById(userDto.getId());
        assertThat(user).isNotNull();

        userService.deleteUser(userDto.getId());

        UserDto finalUserDto = userDto;
        assertThrows(NullPointerException.class, () -> userService.findUserById(finalUserDto.getId()));

        newUserRequestDto = newUserRequestDto.setName("UserName2").setEmail("user@email.user2");
        userDto = userService.createUser(newUserRequestDto);
        UserDto userDto2 = userService.createUser(new NewUserRequestDto()
                .setName("User name1")
                .setEmail("user2email.user1"));

        assertEquals(2, userService.getUsers(List.of(userDto.getId(), userDto2.getId()), 0, 10).size());
    }
}
