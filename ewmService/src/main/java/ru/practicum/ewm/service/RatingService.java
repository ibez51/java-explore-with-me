package ru.practicum.ewm.service;

import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Map;

public interface RatingService {
    void createOrUpdateRate(Event event, User user, Boolean isGood);

    void removeRate(Event event, User user);

    Map<Integer, Integer> calcRateForEventList(List<Integer> eventIdList);

    Map<Integer, Integer> calcRateForUserList(List<Integer> userIdList);
}
