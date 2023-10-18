package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
	public void setUp(){
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("업로드 컨트롤러 테스트")
	void uploadFile(){
		ResponseEntity<Void> response = fileController.uploadFile(FileUploadRequest.of(mockMultipartFile, userId));
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(fileService, times(1)).uploadFile(any());
	}

	@Test
	@DisplayName("파일 다운로드 컨트롤러 테스트")
	void downloadFile(){
		ResponseEntity<Void> response = fileController.downloadFile(FileDownloadRequest.of(fileId, userPath, userId));
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(fileService, times(1)).downloadFile(any());
	}

	@Test
	@DisplayName("삭제 컨트롤러 테스트")
	void deleteFile(){
		ResponseEntity<Void> response = fileController.deleteFile(FileDeleteRequest.of(fileId, userId));
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		verify(fileService, times(1)).deleteFile(any());
	}
}
