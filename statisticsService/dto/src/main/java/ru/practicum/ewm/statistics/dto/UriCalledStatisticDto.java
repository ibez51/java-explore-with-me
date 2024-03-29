package ru.practicum.ewm.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UriCalledStatisticDto {
    private String app;
    private String uri;
    private long hits;
}
