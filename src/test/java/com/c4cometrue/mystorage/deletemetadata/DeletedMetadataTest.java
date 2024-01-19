package com.c4cometrue.mystorage.deletemetadata;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.c4cometrue.mystorage.common.MetadataType;
import com.c4cometrue.mystorage.deletedmetadata.DeletedMetadata;

class DeletedMetadataTest {

	@Test
	@DisplayName("삭제메타데이터 생성 테스트")
	void DeletedMetadataEntityTest() {
		Long userId = 1L;
		Long parentId = 1L;
		String filePath = "testPath";
		MetadataType type = MetadataType.FILE;

		DeletedMetadata deletedMetadata = DeletedMetadata.builder()
			.userId(userId)
			.parentId(parentId)
			.filePath(filePath)
			.type(type)
			.build();

		assertEquals(userId, deletedMetadata.getUserId());
		assertEquals(parentId, deletedMetadata.getParentId());
		assertEquals(filePath, deletedMetadata.getFilePath());
		assertEquals(type, deletedMetadata.getType());

		deletedMetadata.prePersist();

		LocalDate deletedDate = deletedMetadata.getDeletedDate();
		assertNotNull(deletedDate);
		assertEquals(LocalDate.now(), deletedDate);
	}
}
