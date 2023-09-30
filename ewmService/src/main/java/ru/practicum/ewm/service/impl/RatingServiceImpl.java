package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dao.RatingRepository;
import ru.practicum.ewm.dto.rating.RatingMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.service.RatingService;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    @Transactional
    public void createOrUpdateRate(Event event,
                                   User user,
                                   Boolean isGood) {
        ratingRepository.save(ratingMapper.toRating(event, user, isGood ? 1 : 0));
    }

    @Override
    @Transactional
    public void removeRate(Event event,
                           User user) {
        ratingRepository.delete(ratingMapper.toRating(event, user, 1));
    }

    @Override
    public Map<Integer, Integer> calcRateForEventList(List<Integer> eventIdList) {
        if (Objects.isNull(eventIdList)
                || eventIdList.size() == 0) {
            return Map.of();
        }
        List<Object[]> list = ratingRepository.calcRateForEventList(eventIdList);

        return list.stream()
                .collect(Collectors.toMap(
                        x -> ((BigInteger) x[0]).intValue(),
                        x -> {
                            int sum = ((BigInteger) x[1]).intValue();
                            Integer count = ((BigInteger) x[2]).intValue();
                            if (count.equals(0)) {
                                return 0;
                            } else {
                                return (sum * 100) / count;
                            }
                        }
                ));
    }

    @Override
    public Map<Integer, Integer> calcRateForUserList(List<Integer> userIdList) {
        if (Objects.isNull(userIdList)
                || userIdList.size() == 0) {
            return Map.of();
        }
        List<Object[]> list = ratingRepository.calcRateForUserList(userIdList);

        return list.stream()
                .collect(Collectors.toMap(
                        x -> ((BigInteger) x[0]).intValue(),
                        x -> {
                            int sum = ((BigInteger) x[1]).intValue();
                            Integer count = ((BigInteger) x[2]).intValue();
                            if (count.equals(0)) {
                                return 0;
                            } else {
                                return (sum * 100) / count;
                            }
                        }
                ));
    }
}
