package com.c4cometrue.mystorage.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ch.qos.logback.classic.Logger;

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
