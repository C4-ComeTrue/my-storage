package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("파일 엔티티 테스트")
class MetadataTest {
	@Test
	@DisplayName("디비에 저장된 이름")
	void storedName() {
		String result = Metadata.storedName();
		assertTrue(Pattern.matches(uuidPattern, result));
	}

	@Test
	@DisplayName("메타데이터 생성")
	void testMetadataCreation() {
		Metadata data = Metadata.of(OriginalFileName, storedFileName,
			userPath, userId);
		Assertions.assertNotNull(data);
		Assertions.assertEquals(OriginalFileName, data.getOriginalFileName());
		Assertions.assertEquals(storedFileName, data.getStoredFileName());
		Assertions.assertEquals(userPath, data.getFilePath());
		Assertions.assertEquals(userId, data.getUploaderId());
	}

}
