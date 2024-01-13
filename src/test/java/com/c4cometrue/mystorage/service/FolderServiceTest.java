package com.c4cometrue.mystorage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;
import com.c4cometrue.mystorage.repository.FileMetaDataReader;
import com.c4cometrue.mystorage.repository.FileMetaDataWriter;
import com.c4cometrue.mystorage.utils.FileUtil;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

	@Mock
	PathService pathService;

	@Mock
	FileUtil fileUtil;

	@Mock
	FileMetaDataReader folderReader;

	@Mock
	FileMetaDataWriter folderWriter;

	@InjectMocks
	FolderService folderService;

	@Test
	void 루트_폴더_생성() {
		// given
		var userId = 1L;
		var folderId = 1L;
		var folderName = "user";
		var fullPath = "C://user";
		var folderMetaData = mock(FileMetaData.class);

		given(folderMetaData.getId()).willReturn(folderId);
		given(pathService.getFullFilePath(anyString(), anyString())).willReturn(fullPath);
		given(folderReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(false);
		given(folderWriter.saveFolderMetaData(anyLong(), anyString(), any())).willReturn(folderMetaData);

		// when
		var response = folderService.createRootFolder(userId, folderName);

		// then
		assertThat(response)
			.matches(folder -> folder.id() == folderId);
	}

	@Test
	void 부모가_폴더가_아니라면_에러가_발생한다() {
		// given
		var userId = 1L;
		var parentId = 2L;
		var folderName = "name";
		var folderMetaData = mock(FileMetaData.class);

		given(folderMetaData.getFileType()).willReturn(FileType.FILE);
		given(folderReader.get(anyLong(), anyLong())).willReturn(folderMetaData);

		// when + then
		assertThatThrownBy(() -> {
			folderService.createFolder(userId, parentId, folderName);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.INVALID_TYPE.getMsg());
	}

	@Test
	void 저장하려는_폴더_이름이_중복된다면_에러가_발생한다() {
		// given
		var userId = 1L;
		var parentId = 2L;
		var folderName = "name";
		var folderMetaData = mock(FileMetaData.class);

		given(folderMetaData.getFileType()).willReturn(FileType.FOLDER);
		given(folderReader.get(anyLong(), anyLong())).willReturn(folderMetaData);
		given(folderReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(true);

		// when + then
		assertThatThrownBy(() -> {
			folderService.createFolder(userId, parentId, folderName);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.DUPLICATE_FOLDER.getMsg());
	}

	@Test
	void 폴더를_생성한다() {
		// given
		var userId = 1L;
		var folderId = 1L;
		var parentId = 2L;
		var folderName = "folder";
		var folderMetaData = mock(FileMetaData.class);

		given(folderMetaData.getId()).willReturn(folderId);
		given(folderMetaData.getFileType()).willReturn(FileType.FOLDER);

		given(folderReader.get(anyLong(), anyLong())).willReturn(folderMetaData);
		given(folderReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(false);
		given(folderWriter.saveFolderMetaData(anyLong(), anyString(), any())).willReturn(folderMetaData);

		// when
		var response = folderService.createFolder(userId, parentId, folderName);

		// then
		assertThat(response)
			.matches(folder -> folder.id() == folderId);
	}

	@Test
	void 폴더_이름_수정() {
		// given
		var userId = 1L;
		var parentId = 2L;
		var folderName = "folder";
		var folderMetaData = mock(FileMetaData.class);
		var parentMetaData = mock(FileMetaData.class);

		given(folderMetaData.getFileType()).willReturn(FileType.FOLDER);
		given(folderMetaData.getParent()).willReturn(parentMetaData);

		given(folderReader.get(anyLong(), anyLong())).willReturn(folderMetaData);
		given(folderReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(false);

		// when
		folderService.renameFolder(userId, parentId, folderName);

		// then
		verify(folderMetaData, times(1)).rename(anyString());
	}

	@Test
	void 폴더_내용_조회() {
		// given
		var userId = 1L;
		var folderId = 1L;
		var fileId = 2L;
		var fileName = "file";
		var createdAt = LocalDateTime.now();
		var fileSize = 1000L;
		var folderMetaData = mock(FileMetaData.class);
		var fileMetaData = mock(FileMetaData.class);
		var files = List.of(fileMetaData, fileMetaData);

		given(folderMetaData.getId()).willReturn(folderId);
		given(folderMetaData.getFileName()).willReturn("folder");
		given(folderMetaData.getFileType()).willReturn(FileType.FOLDER);

		given(fileMetaData.getId()).willReturn(fileId);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);
		given(fileMetaData.getFileName()).willReturn(fileName);
		given(fileMetaData.getCreatedAt()).willReturn(createdAt);
		given(fileMetaData.getSize()).willReturn(fileSize);

		given(folderReader.get(anyLong(), anyLong())).willReturn(folderMetaData);
		given(folderReader.getFiles(anyLong(), any())).willReturn(files);

		// when
		var response = folderService.getFolderContents(userId, folderId);

		// then
		assertThat(response)
			.matches(folder -> folder.folderId() == folderId)
			.matches(folder -> folder.subFileList().get(0).fileId() == fileId)
			.matches(folder -> Objects.equals(folder.subFileList().get(0).fileName(), fileName))
			.matches(folder -> Objects.equals(folder.subFileList().get(0).createdAt(),
				DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(createdAt)))
			.matches(folder -> folder.subFileList().get(0).fileSize() == fileSize);

	}
}
