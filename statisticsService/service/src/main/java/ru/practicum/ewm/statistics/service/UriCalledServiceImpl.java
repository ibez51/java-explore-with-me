package ru.practicum.ewm.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.statistics.dto.UriCalledDto;
import ru.practicum.ewm.statistics.dto.UriCalledStatisticDto;
import ru.practicum.ewm.statistics.service.model.UriCalled;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UriCalledServiceImpl implements UriCalledService {
    @Autowired
    private UriCalledRepository uriCalledRepository;
    @Autowired
    private UriCalledMapper uriCalledMapper;

    @Override
    @Transactional
    public void saveUriCall(UriCalledDto uriCalledDto) {
        uriCalledRepository.save(uriCalledMapper.toUriCalled(uriCalledDto));
    }

    @Override
    public List<UriCalledStatisticDto> getStats(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uriList,
                                                boolean isUnique) {
        if (start.isAfter(end)
                || start.isEqual(end)) {
            throw new InvalidParameterException("Start date must be before end date");
        }
        List<Object[]> uriCalledList;

        if (Objects.isNull(uriList)) {
            uriCalledList = isUnique ?
                    uriCalledRepository.getUniqueStats(start, end) :
                    uriCalledRepository.getStats(start, end);
        } else {
            uriCalledList = isUnique ?
                    uriCalledRepository.getUniqueStats(start, end, uriList) :
                    uriCalledRepository.getStats(start, end, uriList);
        }

        return uriCalledList.stream()
                .map(x -> new UriCalled()
                        .setApp((String) x[0])
                        .setUri((String) x[1])
                        .setId(((BigInteger) x[2]).longValue()))
                .map(uriCalledMapper::toUriCalledStatisticsDto)
                .collect(Collectors.toList());
    }
}
