package com.c4cometrue.mystorage.common.exception.handler;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handle(BusinessException ex) {
        log.error(ex.getDebugMessage(), ex);
        val errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorCode.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(Exception ex) {
        log.error(ex.getMessage(), ex);
        if (isValidException(ex)) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    private boolean isValidException(Exception ex) {
        return Objects.equals(ex.getClass(), MethodArgumentNotValidException.class)
                || Objects.equals(ex.getClass(),ConstraintViolationException.class)
                || Objects.equals(ex.getClass(), MethodArgumentTypeMismatchException.class);
    }

}
