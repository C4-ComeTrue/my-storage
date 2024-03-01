package com.c4cometrue.mystorage.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ch.qos.logback.classic.Logger;

// 모든 컨트롤러의 예외 처리
@RestControllerAdvice
public class ExceptionHandlingController {
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());
    final ZoneId timeZone = ZoneId.of("Asia/Seoul");

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiExceptionRes> handleServiceException(ServiceException exception) {
        if (exception.getDebugMessage() != null) {
            logger.error(exception.getMessage());
        }

        var apiExceptionRes = new ApiExceptionRes(
            exception.getErrMessage(),
            ZonedDateTime.now(timeZone)
        );
        var httpStatus = ErrorCd.valueOf(exception.getErrCode()).getHttpStatus();

        return new ResponseEntity<>(apiExceptionRes, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiExceptionRes> handleValidException(MethodArgumentNotValidException exception) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errorMessageBuilder.append(fieldName).append(" : ").append(errorMessage).append("\n");
        });

        if (!errorMessageBuilder.isEmpty()) {
            String errorMessages = errorMessageBuilder.toString();
            logger.error(errorMessages);
        }

        var apiExceptionRes = new ApiExceptionRes(
            "Request is not Valid. Please Check Again",
            ZonedDateTime.now(timeZone)
        );

        return new ResponseEntity<>(apiExceptionRes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler()
    public ResponseEntity<ApiExceptionRes> handleException(Exception exception) {
        logger.error(exception.getMessage());
        var apiExceptionRes = new ApiExceptionRes(
            "Sorry, something went wrong",
            ZonedDateTime.now(timeZone)
        );

        return new ResponseEntity<>(apiExceptionRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
