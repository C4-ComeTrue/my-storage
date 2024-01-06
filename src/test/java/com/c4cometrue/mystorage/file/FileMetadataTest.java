package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.c4cometrue.mystorage.TestConstants;

@DisplayName("파일 엔티티 테스트")
class FileMetadataTest {
	@Test
	@DisplayName("디비에 저장된 이름")
	void storedName() {
		String result = FileMetadata.storedName();
		assertTrue(Pattern.matches(UUID_PATTERN, result));
	}

	@Test
	@DisplayName("메타데이터 생성")
	void metadataCreationTest() {
		FileMetadata data = FILE_METADATA;
		Assertions.assertNotNull(data);
		Assertions.assertEquals(ORIGINAL_FILE_NAME, data.getOriginalFileName());
		Assertions.assertEquals(STORED_FILE_NAME, data.getStoredFileName());
		Assertions.assertEquals(USER_PATH, data.getFilePath());
		Assertions.assertEquals(USER_ID, data.getUploaderId());
	}
}
