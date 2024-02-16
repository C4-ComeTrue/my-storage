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

import com.c4cometrue.mystorage.dto.request.file.FileReq;
import com.c4cometrue.mystorage.dto.request.file.MoveFileReq;
import com.c4cometrue.mystorage.dto.request.file.UploadFileReq;
import com.c4cometrue.mystorage.dto.response.file.FileDownloadRes;
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
		var req = new UploadFileReq(MOCK_MULTIPART_FILE, MOCK_USER_NAME, 1L);
		fileController.uploadFile(req);

		// 테스트 결과 검증
		verify(fileService, times(1)).uploadFile(req.file(), req.userName(),
			req.folderId());
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		var req = new FileReq(1L, MOCK_USER_NAME, 1L);

		// when
		fileController.deleteFile(req);

		// then
		verify(fileService, times(1)).deleteFile(req.fileId(), req.userName(), req.folderId());
	}

	@Test
	@DisplayName("파일 다운로드")
	void downloadFile() {
		// given
		var req = new FileReq(1L, MOCK_USER_NAME, 1L);
		given(fileService.downloadFile(req.fileId(), req.userName(), req.folderId())).willReturn(
			new FileDownloadRes(mock(Resource.class), MOCK_FILE_NAME, MOCK_CONTENT_TYPE));

		// when
		fileController.downloadFile(req);

		// then
		verify(fileService, times(1)).downloadFile(req.fileId(), req.userName(), req.folderId());
	}

	@Test
	@DisplayName("파일 이동")
	void moveFile() {
		// given
		var req = new MoveFileReq(1L, 1L, MOCK_USER_NAME);

		// when
		fileController.moveFile(req);

		// then
		verify(fileService, times(1)).moveFile(req.fileId(), req.folderId(), req.userName());
	}
}
