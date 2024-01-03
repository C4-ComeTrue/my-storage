package com.c4cometrue.mystorage.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;

@SpringBootTest
class FileMetaDataReaderTest {

	@Autowired
	FileMetaDataRepository repository;

	@Autowired
	FileMetaDataReader fileMetaDataReader;

	@Test
	void 저장된_파일이_아니라면_조회에_실패한다() {
		// given
		var fileId = 1L;
		var userId = 1L;
		repository.deleteAll();

		// when + then
		assertThatThrownBy(() -> {
			fileMetaDataReader.get(fileId, userId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.FILE_NOT_FOUND.getMsg());
	}

	@Test
	void 저장된_파일을_조회한다() {
		// given
		var userId = 1L;
		var fileName = "name";
		var fileMetaData = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(fileName)
			.fileType(FileType.FILE)
			.build();

		var fileEntity = repository.save(fileMetaData);

		// when
		var response = fileMetaDataReader.get(fileEntity.getId(), userId);

		// then
		assertThat(response)
			.matches(file -> file.getUserId() == userId)
			.matches(file -> Objects.equals(file.getFileName(), fileName));
	}

	@Test
	void 저장된_루트_폴더가_아니라면_조회에_실패한다() {
		// given
		var userId = 1L;

		// when + then
		assertThatThrownBy(() -> {
			fileMetaDataReader.getRootFolder(userId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.FOLDER_NOT_FOUND.getMsg());
	}

	@Test
	void 루트_폴더를_조회한다() {
		// given
		var userId = 1L;
		var fileName = "name";
		var folderMetaData = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(fileName)
			.parent(null)
			.fileType(FileType.FOLDER)
			.build();

		var rootFolder = repository.save(folderMetaData);

		// when
		var response = fileMetaDataReader.getRootFolder(userId);

		// then
		assertThat(response)
			.matches(folder -> Objects.equals(folder.getId(), rootFolder.getId()))
			.matches(folder -> folder.getUserId() == userId)
			.matches(folder -> Objects.equals(folder.getFileName(), fileName));
	}

	@Test
	void 중복된_파일을_감지한다() {
		// given
		var userId = 1L;
		var fileName = "name";
		var parentFolder = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(fileName)
			.fileType(FileType.FOLDER)
			.build();

		var parentEntity = repository.save(parentFolder);
		var fileMetaData = new FileMetaData(userId, fileName, fileName,
			100L, "type", FileType.FILE, parentEntity);

		repository.save(fileMetaData);

		// when
		var response = fileMetaDataReader.isDuplicateFile(fileName, userId, parentEntity);

		// then
		assertThat(response).isTrue();
	}

	@Test
	void 폴더_내부에_존재하는_파일을_반환한다() {
		// given
		var userId = 1L;
		var fileName = "name";
		var parentFolder = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(fileName)
			.fileType(FileType.FOLDER)
			.build();

		var parentEntity = repository.save(parentFolder);
		var fileMetaData = new FileMetaData(userId, fileName, fileName,
			100L, "type", FileType.FILE, parentEntity);

		repository.save(fileMetaData);

		// when
		var response = fileMetaDataReader.getFiles(userId, parentEntity);

		// then
		assertThat(response)
			.matches(files -> files.get(0).getUserId() == userId)
			.matches(files -> Objects.equals(files.get(0).getFileName(), fileName))
			.matches(files -> Objects.equals(files.get(0).getFileType(), FileType.FILE));
	}

}
