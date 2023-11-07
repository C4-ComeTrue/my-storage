package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import com.c4cometrue.mystorage.dto.request.FileReq;
import com.c4cometrue.mystorage.dto.request.UploadFileReq;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.service.FileService;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
	@InjectMocks
	private FileController fileController;
	@Mock
	private FileService fileService;

	@Test
	@DisplayName("파일 업로드")
	void uploadFile() {
		// then
		var uploadFileReq = new UploadFileReq(mockMultipartFile, mockUserName, 1L);
		fileController.uploadFile(uploadFileReq);

		// 테스트 결과 검증
		verify(fileService, times(1)).uploadFile(uploadFileReq);
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);

		// when
		fileController.deleteFile(mockFileReq);

		// then
		verify(fileService, times(1)).deleteFile(mockFileReq);
	}

	@Test
	@DisplayName("파일 다운로드")
	void downloadFile() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);
		given(fileService.downloadFile(mockFileReq)).willReturn(
			new FileDownloadRes(mock(Resource.class), mockFileName, mockContentType));

		// when
		fileController.downloadFile(mockFileReq);

		// then
		verify(fileService, times(1)).downloadFile(mockFileReq);
	}
}
