package com.c4cometrue.mystorage.exception;

import ch.qos.logback.classic.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class ServiceExceptionController {

    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException exception) {
        if (exception.getDebugMessage() != null) {
            logger.debug(exception.getMessage());
        }
        var errorResponse = new ErrorResponse(
                exception.getErrorMessage(),
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        var httpStatus = ErrorCode.valueOf(exception.getErrorCode()).getHttpStatus();

        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ErrorResponse> handleValidException(Exception exception) {
        var ErrorResponse = new ErrorResponse(
                exception.getMessage(),
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );
        return new ResponseEntity<>(ErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
