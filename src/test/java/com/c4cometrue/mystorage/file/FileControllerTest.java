package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;

@DisplayName("파일 컨트롤러 테스트")
class FileControllerTest {
	@InjectMocks
	private FileController fileController;

	@Mock
	private FileService fileService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("업로드 컨트롤러 테스트")
	void uploadFile() {
		fileController.uploadFile(FileUploadRequest.of(MOCK_MULTIPART_FILE, USER_ID));
		verify(fileService, times(1)).uploadFile(any(), anyLong());
	}

	@Test
	@DisplayName("파일 다운로드 컨트롤러 테스트")
	void downloadFile() {
		fileController.downloadFile(FileDownloadRequest.of(FILE_ID, USER_PATH, USER_ID));
		verify(fileService, times(1)).downloadFile(anyLong(), anyString(), anyLong());
	}

	@Test
	@DisplayName("삭제 컨트롤러 테스트")
	void deleteFile() {
		fileController.deleteFile(FileDeleteRequest.of(FILE_ID, USER_ID));
		verify(fileService, times(1)).deleteFile(anyLong(), anyLong());
	}
}
