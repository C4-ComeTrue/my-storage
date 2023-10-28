package com.c4cometrue.mystorage.exception;


import com.sun.net.httpserver.HttpsServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ServiceExceptionControllerTest {

    @InjectMocks
    ServiceExceptionController exceptionController;

    @Test
    void serviceException() {
        // given
        var mockException = mock(ServiceException.class);
        given(mockException.getErrorMessage()).willReturn("File Not Exist");
        given(mockException.getErrorCode()).willReturn("FILE_NOT_EXIST");

        // when
        ResponseEntity<ErrorResponse> response = exceptionController.handleServiceException(mockException);

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("File Not Exist", response.getBody().message());
    }
}
