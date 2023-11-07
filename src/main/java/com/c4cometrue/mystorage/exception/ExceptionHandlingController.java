package com.c4cometrue.mystorage.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ch.qos.logback.classic.Logger;
import jakarta.validation.ValidationException;

// 모든 컨트롤러의 예외 처리
@RestControllerAdvice
public class ExceptionHandlingController {
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());
    final ZoneId timeZone = ZoneId.of("Asia/Seoul");
    // value에 포함된 예외들 처리함
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

    // value에 포함된 예외들 처리함
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiExceptionRes> handleValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .reduce((msg1, msg2) -> msg1 + ", " + msg2)
            .orElse("");

        var apiExceptionRes = new ApiExceptionRes(
            errorMessage,
            ZonedDateTime.now(timeZone)
        );

        return new ResponseEntity<>(apiExceptionRes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiExceptionRes> handleValidException(ValidationException exception) {
        var apiExceptionRes = new ApiExceptionRes(
            "Request is not Valid",
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
