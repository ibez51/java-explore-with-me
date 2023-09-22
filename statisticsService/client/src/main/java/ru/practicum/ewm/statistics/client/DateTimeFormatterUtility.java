package ru.practicum.ewm.statistics.client;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtility {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);
}
