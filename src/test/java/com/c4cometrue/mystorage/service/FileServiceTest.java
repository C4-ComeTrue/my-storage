package com.c4cometrue.mystorage.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;
import com.c4cometrue.mystorage.repository.FileMetaDataReader;
import com.c4cometrue.mystorage.repository.FileMetaDataWriter;
import com.c4cometrue.mystorage.utils.FileUtil;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

	@Mock
	PathService pathService;

	@Mock
	FileUtil fileUtil;

	@Mock
	FileMetaDataReader fileMetaDataReader;

	@Mock
	FileMetaDataWriter fileMetaDataWriter;

	@InjectMocks
	FileService fileService;

	static FileMetaData rootFolder;

	@BeforeAll
	static void init() {
		rootFolder = FileMetaData.rootBuilder()
			.id(1L)
			.userId(1L)
			.fileName("root")
			.uploadName(".")
			.fileType(FileType.FOLDER)
			.build();
	}

	// @Test
	void 파일이_없다면_실패한다() {
		//given
		var userId = 1L;
		var folderId = 1L;
		var file = mock(MultipartFile.class);

		// when + then
		assertThatThrownBy(() -> {
			fileService.fileUpload(file, userId, folderId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.FILE_EMPTY.getMsg());
	}

	@Test
	void 중복된_파일이라면_실패한다() {
		// given
		var userId = 1L;
		var folderId = 1L;
		var file = mock(MultipartFile.class);

		given(fileMetaDataReader.getRootFolder(anyLong())).willReturn(rootFolder);
		given(file.getOriginalFilename()).willReturn("dd.jpg");
		given(fileMetaDataReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(true);

		// when + then
		assertThatThrownBy(() -> {
			fileService.fileUpload(file, userId, folderId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.DUPLICATE_FILE.getMsg());
	}

	@Test
	void 파일_메타데이터를_등록한다() {
		// given
		var fileId = 2L;
		var folderId = rootFolder.getId();
		var userId = 1L;
		var file = mock(MultipartFile.class);
		var originFileName = "dd.jpg";
		var uploadFileName = "ddd.jpg";
		var size = 1000L;
		var fileMetaData = mock(FileMetaData.class);

		given(file.getOriginalFilename()).willReturn(originFileName);
		given(fileMetaData.getId()).willReturn(fileId);
		given(fileMetaData.getUserId()).willReturn(userId);
		given(fileMetaData.getSize()).willReturn(size);
		given(fileMetaData.getUploadName()).willReturn(uploadFileName);

		given(fileMetaDataReader.getRootFolder(anyLong())).willReturn(rootFolder);
		given(fileMetaDataWriter.saveFileMetaData(any(), anyLong(), anyString(), any())).willReturn(fileMetaData);
		given(fileMetaDataReader.isDuplicateFile(anyString(), anyLong(), any())).willReturn(false);
		given(pathService.createUniqueFileName()).willReturn(uploadFileName);
		given(pathService.getFullFilePath(anyString(), anyString())).willReturn("C://");

		// when
		var response = fileService.fileUpload(file, userId, folderId);

		// then
		assertThat(response)
			.matches(metadata -> StringUtils.contains(metadata.uploadFileName(), originFileName))
			.matches(metadata -> metadata.fileSize() == size)
			.matches(metadata -> metadata.fileId() == fileId)
			.matches(metadata -> metadata.userId() == userId);
	}

	@Test
	void 본인이_업로드한_파일이_아니라면_다운로드에_실패한다() {
		// given
		var userId = 2L;
		var fileId = 1L;
		var fileMetaData = mock(FileMetaData.class);

		given(fileMetaData.getUserId()).willReturn(1L);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);
		given(fileMetaDataReader.get(anyLong(), anyLong())).willReturn(fileMetaData);

		// when + then
		assertThatThrownBy(() -> {
			fileService.fileDownLoad(userId, fileId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.INVALID_FILE_ACCESS.getMsg());
	}

	@Test
	void 파일을_읽어오다가_에러가_발생하면_실패한다() throws IOException {
		// given
		var userId = 1L;
		var fileId = 1L;
		var fileName = "name";
		var resource = mock(UrlResource.class);
		var fileMetaData = mock(FileMetaData.class);

		given(fileMetaData.getUserId()).willReturn(userId);
		given(fileMetaData.getUploadName()).willReturn(fileName);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);

		given(fileMetaDataReader.get(anyLong(), anyLong())).willReturn(fileMetaData);
		given(fileMetaDataReader.getRootFolder(anyLong())).willReturn(rootFolder);
		given(pathService.getFullFilePath(anyString(), anyString())).willReturn("C://");
		given(fileUtil.downloadFile(anyString())).willReturn(resource);
		given(resource.getContentAsByteArray()).willThrow(IOException.class);

		// when
		var ex = assertThrows(BusinessException.class,
			() -> fileService.fileDownLoad(userId, fileId));

		// then
		assertEquals(ErrorCode.FILE_DOWNLOAD_FAILED, ex.getErrorCode());
	}

	@Test
	void 파일을_다운로드에_성공한다() throws IOException {
		// given
		var userId = 1L;
		var fileId = 1L;
		var contentType = MediaType.IMAGE_JPEG.getType();
		var fileName = "name.jpg";
		var fileMetaData = mock(FileMetaData.class);

		given(fileMetaData.getUserId()).willReturn(userId);
		given(fileMetaData.getUploadName()).willReturn(fileName);
		given(fileMetaData.getType()).willReturn(contentType);
		given(fileMetaData.getUserId()).willReturn(userId);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);

		given(fileMetaDataReader.get(anyLong(), anyLong())).willReturn(fileMetaData);
		given(fileMetaDataReader.getRootFolder(anyLong())).willReturn(rootFolder);
		given(pathService.getFullFilePath(anyString(), anyString())).willReturn("C://");

		var byteArray = new byte[1];
		var resource = mock(UrlResource.class);
		lenient().when(resource.getFilename()).thenReturn(fileName);
		lenient().when(resource.getContentAsByteArray()).thenReturn(byteArray);
		given(fileUtil.downloadFile(anyString())).willReturn(resource);

		// when
		var response = fileService.fileDownLoad(userId, fileId);

		// then
		assertThat(response)
			.matches(res -> res.contentType().equals(contentType))
			.matches(res -> Arrays.equals(res.data().bytes(), byteArray));
	}

	@Test
	void 본인이_업로드한_파일이_아니라면_삭제에_실패한다() {
		// given
		var userId = 2L;
		var fileId = 1L;
		var fileMetaData = mock(FileMetaData.class);

		given(fileMetaData.getUserId()).willReturn(1L);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);
		given(fileMetaDataReader.get(anyLong(), anyLong())).willReturn(fileMetaData);

		// when + then
		assertThatThrownBy(() -> {
			fileService.fileDelete(userId, fileId);
		}).isInstanceOf(BusinessException.class)
			.hasMessageContaining(ErrorCode.INVALID_FILE_ACCESS.getMsg());
	}

	@Test
	void 파일_삭제에_성공한다() {
		// given
		var userId = 1L;
		var fileId = 1L;
		var uploadName = "file";
		var fileMetaData = mock(FileMetaData.class);

		given(fileMetaData.getUserId()).willReturn(userId);
		given(fileMetaData.getUploadName()).willReturn(uploadName);
		given(fileMetaData.getFileType()).willReturn(FileType.FILE);
		given(fileMetaDataReader.get(anyLong(), anyLong())).willReturn(fileMetaData);

		// when
		fileService.fileDelete(userId, fileId);

		// then
		verify(fileUtil, times(1)).deleteFile(anyString());
	}
}
