package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;
import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("파일 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileServiceTest {
	@Mock
	private FileDataAccessService fileDataAccessService;

	@InjectMocks
	private FileService fileService;

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(fileService, "storagePath", "/path/to/storage");
		ReflectionTestUtils.setField(fileService, "bufferSize", 1024);
	}

	@Test
	@DisplayName("업로드 실패 테스트")
	void uploadFileFailTest() throws IOException {
		FileUploadRequest request = FileUploadRequest.of(mockMultipartFile, userId);
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(request)
		);

		assertEquals(ErrorCode.FILE_COPY_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("다운로드 실패 테스트")
	void downloadFileFailTest() throws IOException {
		FileDownloadRequest request = mock(FileDownloadRequest.class);
		when(request.fileId()).thenReturn(fileId);
		when(request.userId()).thenReturn(userId);
		when(request.userPath()).thenReturn(userPath);

		Metadata mockMetadata = METADATA;
		when(fileDataAccessService.findBy(anyLong())).thenReturn(mockMetadata);

		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.downloadFile(request)
		);
		assertEquals(ErrorCode.FILE_COPY_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("삭제 실패 테스트")
	void deleteFileFailTest() throws IOException {
		FileDeleteRequest request = FileDeleteRequest.of(fileId, userId);
		when(fileDataAccessService.findBy(fileId)).thenReturn(METADATA);
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.deleteFile(request)
		);

		assertEquals(ErrorCode.FILE_DELETE_ERROR, thrown.getCode());
	}

	@Test
	@DisplayName("업로드 실패 테스트")
	void uploadFileSuccessTest() throws IOException {
		// 모킹 설정
		MultipartFile mockFile = mock(MultipartFile.class);
		when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(mockMultipartFile.getBytes()));
		when(mockFile.getOriginalFilename()).thenReturn(OriginalFileName);

		FileUploadRequest request = FileUploadRequest.of(mockFile, userId);

		doNothing().when(fileDataAccessService).persist(any(Metadata.class));
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(request)
		);
	}
}
