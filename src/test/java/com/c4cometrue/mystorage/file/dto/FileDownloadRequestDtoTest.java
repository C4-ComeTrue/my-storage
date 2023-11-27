package com.c4cometrue.mystorage.file.dto;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileDownloadRequestDtoTest {

	@Mock
	FileDownloadRequestDto fileDownloadRequestDto;

	@DisplayName("파일다운로드 요청dto 생성 테스트")
	@Test
	void creatTest() {
		// given
		FileDownloadRequestDto dto = fileDownloadRequestDto.create(MOCK_FILE_NAME, MOCK_USER_NAME, MOCK_DOWN_ROOT_PATH);

		//then
		assertEquals(MOCK_FILE_NAME, dto.fileName());
		assertEquals(MOCK_USER_NAME, dto.userName());
		assertEquals(MOCK_DOWN_ROOT_PATH, dto.downloadPath());
	}
}
