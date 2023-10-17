package com.c4cometrue.mystorage.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

// 모든 컨트롤러의 예외 처리
@RestControllerAdvice
public class ExceptionHandlingController {
    // value에 포함된 예외들 처리함
    @ExceptionHandler(value = {FileException.class})
    public ResponseEntity<Object> handleFileException(FileException e) {
        // 1. 예외 세부 사항을 포함한 페이로드
        ApiExceptionRes apiExceptionRes = new ApiExceptionRes(
                e.getMessage(),
                e.getHttpStatus(),
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );
        // 2. ResponseEntity에 담아서 반환
        return new ResponseEntity<>(apiExceptionRes, e.getHttpStatus());
    }

    // value에 포함된 예외들 처리함
    @ExceptionHandler(value = {ConstraintViolationException.class, MissingServletRequestPartException.class})
    public ResponseEntity<Object> handleValidException(Exception e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        // 1. 예외 세부 사항을 포함한 페이로드
        ApiExceptionRes apiExceptionRes = new ApiExceptionRes(
                "잘 못된 요청입니다.",
                badRequest,
                ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        );
        // 2. ResponseEntity에 담아서 반환
        return new ResponseEntity<>(apiExceptionRes, badRequest);
    }
}
