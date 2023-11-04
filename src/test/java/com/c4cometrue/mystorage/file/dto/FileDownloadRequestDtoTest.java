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
		FileDownloadRequestDto dto = fileDownloadRequestDto.create(mockFileName, mockUserName, mockDownRootPath);

		//then
		assertEquals(mockFileName, dto.fileName());
		assertEquals(mockUserName, dto.userName());
		assertEquals(mockDownRootPath, dto.downloadPath());
	}
}
