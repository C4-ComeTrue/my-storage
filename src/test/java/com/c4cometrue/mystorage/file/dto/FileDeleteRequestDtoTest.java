package com.c4cometrue.mystorage.file.dto;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileDeleteRequestDtoTest {

	@Mock
	FileDeleteRequestDto fileDeleteRequestDto;

	@DisplayName("파일삭제 요청dto 생성 테스트")
	@Test
	void createTest() {
		// 생성
		FileDeleteRequestDto dto = fileDeleteRequestDto.create(MOCK_FILE_NAME, MOCK_USER_NAME);

		// matches
		assertEquals(MOCK_FILE_NAME, dto.fileName());
		assertEquals(MOCK_USER_NAME, dto.userName());
	}

}
