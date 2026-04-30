package com.unipapers.backend.Exceptions;

import com.unipapers.backend.Exceptions.CustomExceptions.CourseNotFoundException;
import com.unipapers.backend.Exceptions.CustomExceptions.InvalidRefreshTokenException;
import com.unipapers.backend.Exceptions.CustomExceptions.PastPaperNotFoundException;
import com.unipapers.backend.Exceptions.Models.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        log.warn("Invalid request data: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshTokenException(
            InvalidRefreshTokenException ex,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        log.warn("Invalid refresh token: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred",
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        // Log full stacktrace internally
        log.error("Unexpected error", ex);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiError> handleCourseNotFoundException(
            CourseNotFoundException ex,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        // Log the error message internally
        log.warn("Course not found: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PastPaperNotFoundException.class)
    public ResponseEntity<ApiError> handlePastPaperNotFoundException(
            PastPaperNotFoundException ex,
            HttpServletRequest request
    ) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                UUID.randomUUID().toString()
        );

        log.warn("Past paper not found: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

}
