package com.c4cometrue.mystorage.exception;


import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import ch.qos.logback.classic.Logger;
import jakarta.validation.ConstraintViolationException;



// 모든 컨트롤러의 예외 처리
@RestControllerAdvice
public class ExceptionHandlingController {
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    // value에 포함된 예외들 처리함
    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ApiExceptionRes> handleServiceException(ServiceException exception) {
        if (exception.getDebugMessage() != null) {
            logger.debug(exception.getMessage());
        }

        var apiExceptionRes = new ApiExceptionRes(
            exception.getErrMessage(),
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );
        var httpStatus = ErrorCd.valueOf(exception.getErrCode()).getHttpStatus();

        return new ResponseEntity<>(apiExceptionRes, httpStatus);
    }

    // value에 포함된 예외들 처리함
    @ExceptionHandler(value = {ConstraintViolationException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiExceptionRes> handleValidException(Exception exception) {
        // 에러 메세지 + 시간
        var apiExceptionRes = new ApiExceptionRes(
            exception.getMessage(),
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );

        return new ResponseEntity<>(apiExceptionRes, HttpStatus.BAD_REQUEST);
    }
}
