package com.c4cometrue.mystorage.file.service;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilePathServiceTest {

	FilePathService filePathService;

	@BeforeEach
	void init() {
		filePathService = new FilePathService(MOCK_ROOT);
	}

	@DisplayName("파일 경로 생성 테스트")
	@Test
	void createSavedPathTest() {
		var path = filePathService.createSavedPath(MOCK_FILE_PATH);

		assertEquals(MOCK_UPLOAD_PATH, path);
	}
}
