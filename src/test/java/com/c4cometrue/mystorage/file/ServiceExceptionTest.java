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
		ServiceException exception = new ServiceException(ErrorCode.CANNOT_FOUND_FILE);

		assertEquals(ErrorCode.CANNOT_FOUND_FILE, exception.getCode());

		ServiceException exceptionWithMessage = new ServiceException(ErrorCode.CANNOT_FOUND_FILE, fileId);
		assertEquals("해당 파일을 찾을 수 없습니다.", exceptionWithMessage.getMessage());
	}
}
