package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Integer> ids,
                           Integer from,
                           Integer size);

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    void deleteUser(Integer userId);

    User findUserById(Integer userId);
}
