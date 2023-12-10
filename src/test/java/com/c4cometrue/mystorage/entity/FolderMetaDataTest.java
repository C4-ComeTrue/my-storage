package com.c4cometrue.mystorage.entity;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class FolderMetaDataTest {

	@Test
	void builderTest() {
		// given
		var folderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.userName("userName")
			.parentFolderId(1L)
			.build();

		// then
		assertThat(folderMetaData)
			.matches(metadata -> StringUtils.equals(metadata.getFolderName(), "my_folder"))
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), MOCK_USER_NAME))
			.matches(metadata -> metadata.getParentFolderId() == 1L);
	}

	@Test
	void setFolderNameTest() {
		// given
		var folderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.userName("userName")
			.parentFolderId(1L)
			.build();
		var otherFolderName = "random_name";

		// when
		folderMetaData.setFolderName(otherFolderName);

		// then
		assertEquals(folderMetaData.getFolderName(), otherFolderName);

	}
}
