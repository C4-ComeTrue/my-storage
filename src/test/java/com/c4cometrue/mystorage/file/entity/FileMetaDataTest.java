package com.c4cometrue.mystorage.file.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class FileMetaDataTest {

	@Test
	@DisplayName("FileMetatData Entity 생성")
	void fileMetaDataBuilderTest() {
		// given : builder메소드로 metadataentity생성하기
		FileMetaData fileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.userName(MOCK_USER_NAME)
			.fileSize(MOCK_SIZE)
			.fileMine(MOCK_CONTENT_TYPE)
			.savedPath(MOCK_FILE_PATH)
			.build();

		// then : assertThat - matches로 검증
		assertThat(fileMetaData)
			.matches((data) -> StringUtils.equals(data.getFileName(), MOCK_FILE_NAME))
			.matches((data) -> StringUtils.equals(data.getUserName(), MOCK_USER_NAME))
			.matches((data) -> data.getFileSize() == MOCK_SIZE)
			.matches((data) -> StringUtils.equals(data.getFileMine(), MOCK_CONTENT_TYPE))
			.matches((data) -> StringUtils.equals(data.getSavedPath(), MOCK_FILE_PATH));
	}
}
