package com.c4cometrue.mystorage.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingControllerTest {
    @InjectMocks
    ExceptionHandlingController exceptionHandlingController;

    @Test
    @DisplayName("서비스 exception ResponseEntity 응답값 확인")
    void serviceException() {
        // given
        var mockException = mock(ServiceException.class);
        given(mockException.getErrMessage()).willReturn("File not exist");
        given(mockException.getErrCode()).willReturn("FILE_NOT_EXIST");

        // when
        ResponseEntity<ApiExceptionRes> response = exceptionHandlingController.handleServiceException(mockException);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("File not exist", response.getBody().message());
    }

    @Test
    @DisplayName("로그 디버깅 메세지")
    void logDebugMessage() {
        // given
        var mockException = mock(ServiceException.class);
        given(mockException.getErrMessage()).willReturn("File not exist");
        given(mockException.getErrCode()).willReturn("FILE_NOT_EXIST");
        given(mockException.getDebugMessage()).willReturn("debug message");


        // when
        var response = exceptionHandlingController.handleServiceException(mockException);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("File not exist", Objects.requireNonNull(response.getBody()).message());
    }

    @Test
    @DisplayName("Javax 유효성 검증 테스트")
    void validationExceptionTest() {
        // given
        var mockException = mock(ValidationException.class);

        // when
        var response = exceptionHandlingController.handleValidException(mockException);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("그 외 모든 예외 테스트")
    void handleExceptionTest() {
        // given
        var mockException = mock(Exception.class);

        // when
        var response = exceptionHandlingController.handleException(mockException);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
