package ru.practicum.ewm.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final NullPointerException ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        log.debug("Got 404 status {} {}", ex.getMessage(), stringWriter);

        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                "The required object was not found.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeError(final Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        log.debug("Got 500 status {} {}", ex.getMessage(), stringWriter);

        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Internal server error.",
                ex.getMessage(),
                LocalDateTime.now());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalStateError(final IllegalStateException ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        log.debug("Got 500 status {} {}", ex.getMessage(), stringWriter);

        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Internal server error.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidError(final MethodArgumentNotValidException ex) {
        log.warn("Got 400 status {}", ex.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidParameterError(final InvalidParameterException ex) {
        log.warn("Got 400 status {}", ex.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterError(final MissingServletRequestParameterException ex) {
        log.warn("Got 400 status {}", ex.getMessage());

        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Incorrectly made request.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationError(final ConstraintViolationException ex) {
        log.warn("Got 409 status {}", ex.getMessage());

        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationError(final DataIntegrityViolationException ex) {
        log.warn("Got 409 status {}", ex.getMessage());

        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "Integrity constraint has been violated.",
                ex.getMessage(),
                LocalDateTime.now());
    }
}
