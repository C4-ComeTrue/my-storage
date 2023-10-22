package com.c4cometrue.mystorage.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingControllerTest {
    @InjectMocks
    ExceptionHandlingController exceptionHandlingController;

    @Test
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
        assertEquals("File not exist", response.getBody().message());
    }

    @Test
    void validExceptionConstraintViolation() {
        // given
        var mockException = mock(ConstraintViolationException.class);

        // when
        var response = exceptionHandlingController.handleValidException(mockException);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void validExceptionMissingServletRequestPart() {
        // given
        var mockException = mock(MissingServletRequestPartException.class);

        // when
        var response = exceptionHandlingController.handleValidException(mockException);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
