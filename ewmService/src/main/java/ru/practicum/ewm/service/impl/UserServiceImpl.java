package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.UserRepository;
import ru.practicum.ewm.dto.user.NewUserRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.service.RatingService;
import ru.practicum.ewm.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RatingService ratingService;

    @Override
    public List<UserDto> getUsers(List<Integer> ids,
                                  Integer from,
                                  Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<User> userList = Objects.nonNull(ids) ?
                userRepository.findByIdInOrderByIdAsc(ids, page).getContent() :
                userRepository.findAll(page).getContent();
        Map<Integer, Integer> userRatingList = ratingService.calcRateForUserList(ids);

        return userList.stream()
                .map(x -> userMapper.toDto(x,
                        userRatingList.getOrDefault(x.getId(), 0)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(NewUserRequestDto newUserRequestDto) {
        User user = userRepository.save(userMapper.toUser(newUserRequestDto));
        return userMapper.toDto(user, 0);
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.isUserExists(userId)) {
            throw new NullPointerException("User with id=" + userId + " was not found");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("User with id=" + userId + " was not found"));
    }
}
