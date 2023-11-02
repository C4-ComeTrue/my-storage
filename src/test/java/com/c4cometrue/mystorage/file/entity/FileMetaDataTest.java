package com.c4cometrue.mystorage.file.entity;

import com.c4cometrue.mystorage.file.entity.FileMetaData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.c4cometrue.mystorage.TestMockFile.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class FileMetaDataTest {

	@Test
	@DisplayName("FileMetatData Entity 생성")
	void fileMetaDataBuilderTest() {
		// given : builder메소드로 metadataentity생성하기
		FileMetaData fileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.userName(mockUserName)
			.fileSize(mockSize)
			.fileMine(mockContentType)
			.savedPath(mockFilePath)
			.build();

		// then : assertThat - matches로 검증
		assertThat(fileMetaData)
			.matches((data) -> StringUtils.equals(data.getFileName(), mockFileName))
			.matches((data) -> StringUtils.equals(data.getUserName(), mockUserName))
			.matches((data) -> data.getFileSize() == mockSize)
			.matches((data) -> StringUtils.equals(data.getFileMine(), mockContentType))
			.matches((data) -> StringUtils.equals(data.getSavedPath(), mockFilePath));
	}
}
