package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.c4cometrue.mystorage.exception.ErrorResponse;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.exception.ServiceExceptionHandler;
@ExtendWith(MockitoExtension.class)
class ServiceExceptionHandlerTest {
	@InjectMocks
	private ServiceExceptionHandler handler;

	@Mock
	private ServiceException serviceException;
	@Test
	@DisplayName("핸들러가 정상적으로 응답을 반환 하는 지 테스트")
	void handleExceptionTest() {
		// Given
		when(serviceException.getErrCode()).thenReturn(ERR_CODE);
		when(serviceException.getErrMessage()).thenReturn(ERR_MESSAGE);

		ErrorResponse expectedResponse = new ErrorResponse(ERR_CODE, ERR_MESSAGE);

		// When
		ResponseEntity<ErrorResponse> responseEntity = handler.handleServiceException(serviceException);

		// Then
		assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
		assertEquals(expectedResponse, responseEntity.getBody());
	}

}
