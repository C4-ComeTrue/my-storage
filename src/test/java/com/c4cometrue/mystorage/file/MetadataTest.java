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
	private Metadata testMetadata;

	@BeforeEach
	void setUp() {
		testMetadata = METADATA;
	}

	@Test
	@DisplayName("승인된 사용자 요쳥")
	void validateWithAuthorizedAccess() {
		assertDoesNotThrow(() -> testMetadata.validate(userId));
	}

	@Test
	@DisplayName("미승인된 사용자 요쳥")
	void validateWithUnauthorizedAccess() {
		ServiceException exception = assertThrows(
			ServiceException.class,
			() -> testMetadata.validate(nonMatchingUserId)
		);

		assertEquals(ErrorCode.UNAUTHORIZED_FILE_ACCESS, exception.getCode());
	}

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
