package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("예외 테스트")
class ServiceExceptionTest {
	@Test
	@DisplayName("에러 코드 테스트")
	void errorCodeTest() {
		assertEquals(HttpStatus.NOT_FOUND, ErrorCode.CANNOT_FOUND_FILE.getHttpStatus());
		assertEquals("해당 파일을 찾을 수 없습니다.", ErrorCode.CANNOT_FOUND_FILE.getMessage());
	}

	@Test
	@DisplayName("서비스 익셉션 테스트")
	void serviceExceptionTest() {
		ServiceException exception = new ServiceException(ErrorCode.CANNOT_FOUND_FILE.name(),
			ErrorCode.CANNOT_FOUND_FILE.getMessage());

		assertEquals(ErrorCode.CANNOT_FOUND_FILE.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("디버그 메시지 테스트")
	void debugMessageTest() {
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED_FILE_ACCESS;

		ServiceException exception = errorCode.serviceException(DEBUG_MESSAGE_TEMPLATE, FILE_ID, USER_ID);

		assertEquals(errorCode.name(), exception.getErrCode());
		assertEquals(errorCode.getMessage(), exception.getErrMessage());
		assertEquals(DEBUG_MESSAGE, exception.getDebugMessage());
	}

	@Test
	@DisplayName("서비스 익셉션 생성자 테스트")
	void testConstructor_withCause() {
		ServiceException exception = new ServiceException(CAUSE, ERR_CODE, ERR_MESSAGE, DEBUG_MESSAGE);

		assertSame(CAUSE, exception.getCause());
		assertEquals(ERR_CODE, exception.getErrCode());
		assertEquals(ERR_MESSAGE, exception.getErrMessage());
		assertEquals(DEBUG_MESSAGE, exception.getDebugMessage());
	}
}
