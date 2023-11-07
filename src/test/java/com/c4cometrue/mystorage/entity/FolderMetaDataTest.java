package com.c4cometrue.mystorage.entity;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class FolderMetaDataTest {

	@Test
	void builderTest() {
		// given
		var mockPath = Path.of(mockRootPath).resolve("userName").resolve("my_folder");
		var folderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.folderPath(mockPath.toString())
			.userName("userName")
			.parentFolderId(1L)
			.build();

		assertThat(folderMetaData)
			.matches(metadata -> metadata.getFolderId() == 0)
			.matches(metadata -> StringUtils.equals(metadata.getFolderName(), "my_folder"))
			.matches(metadata -> StringUtils.equals(metadata.getFolderPath(), mockPath.toString()))
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), mockUserName))
			.matches(metadata -> metadata.getParentFolderId() == 1L);
	}

	@Test
	void setFolderNameTest() {
		// given
		var mockPath = Path.of(mockRootPath).resolve("userName").resolve("my_folder");
		var folderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.folderPath(mockPath.toString())
			.userName("userName")
			.parentFolderId(1L)
			.build();
		var otherFolderName = "random_name";

		// when
		folderMetaData.setFolderName(otherFolderName);

		// then
		assertEquals(folderMetaData.getFolderName(), otherFolderName);

	}

	@Test
	void setFolderPathTest() {
		// given
		Path rootPath = Path.of(mockRootPath);
		var mockPath = rootPath.resolve("userName").resolve("my_folder");
		var folderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.folderPath(mockPath.toString())
			.userName("userName")
			.parentFolderId(1L)
			.build();
		var otherFolderPath = rootPath.resolve("userName").resolve("other_folder");

		// when
		folderMetaData.setFolderPath(otherFolderPath.toString());

		// then
		assertEquals(folderMetaData.getFolderPath(), otherFolderPath.toString());

	}

}
