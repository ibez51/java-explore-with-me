package ru.practicum.ewm.exceptions;

public class InvalidParameterException extends RuntimeException {
    public InvalidParameterException(final String message) {
        super(message);
    }
}