package ru.practicum.ewm.statistics.service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidParameterException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeError(final Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        log.debug("Got 500 status {} {}", ex.getMessage(), stringWriter);

        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidParameterError(final InvalidParameterException ex) {
        log.warn("Got 400 status {}", ex.getMessage());

        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterError(final MissingServletRequestParameterException ex) {
        log.warn("Got 400 status {}", ex.getMessage());

        return new ErrorResponse(ex.getMessage());
    }
}
