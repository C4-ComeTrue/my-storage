package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

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
		ReflectionTestUtils.setField(fileService, "bufferSize", 1024);
	}

	@Test
	@DisplayName("업로드 실패 테스트")
	void uploadFileFailTest() throws IOException {
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(MOCK_MULTIPART_FILE, USER_ID, PARENT_ID, PARENT_PATH)
		);

		assertEquals(ErrorCode.FILE_COPY_ERROR.name(), thrown.getErrCode());
	}

	@Test
	@DisplayName("다운로드 실패 테스트")
	void downloadFileFailTest() throws IOException {
		when(fileDataAccessService.findBy(anyLong(), anyLong())).thenReturn(FILE_METADATA);

		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.downloadFile(FILE_ID, USER_PATH, USER_ID)
		);
		assertEquals(ErrorCode.FILE_COPY_ERROR.name(), thrown.getErrCode());
	}

	@Test
	@DisplayName("삭제 실패 테스트")
	void deleteFileFailTest() throws IOException {
		when(fileDataAccessService.findBy(FILE_ID, USER_ID)).thenReturn(FILE_METADATA);
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.deleteFile(FILE_ID, USER_ID)
		);

		assertEquals(ErrorCode.FILE_DELETE_ERROR.name(), thrown.getErrCode());
	}

	@Test
	@DisplayName("업로드 IO 실패 테스트")
	void uploadFileIoFailTest() throws IOException {
		MultipartFile mockFile = mock(MultipartFile.class);
		when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(MOCK_MULTIPART_FILE.getBytes()));
		when(mockFile.getOriginalFilename()).thenReturn(ORIGINAL_FILE_NAME);

		doNothing().when(fileDataAccessService).persist(any(FileMetadata.class), anyLong(), anyLong());
		ServiceException thrown = assertThrows(
			ServiceException.class,
			() -> fileService.uploadFile(mockFile, USER_ID, PARENT_ID, PARENT_PATH)
		);
	}

	@Test
	@DisplayName("감동의 업로드 성공 테스트")
	void uploadFileTest() throws IOException {
		// given
		var multipartFile = mock(MultipartFile.class);
		var inputStream = mock(InputStream.class);
		var outStream = mock(OutputStream.class);
		var files = mockStatic(Files.class);

		given(multipartFile.getInputStream()).willReturn(inputStream);
		given(multipartFile.getOriginalFilename()).willReturn(ORIGINAL_FILE_NAME);
		given(Files.newOutputStream(any())).willReturn(outStream);
		given(inputStream.read(any()))
			.willReturn(10)
			.willReturn(20)
			.willReturn(30)
			.willReturn(-1);

		// when
		fileService.uploadFile(multipartFile, USER_ID, PARENT_ID, PARENT_PATH);

		// then
		then(outStream).should(times(3)).write(any(), eq(0), anyInt());

		files.close();
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void getFile() {
		given(fileDataAccessService.findChildBy(PARENT_ID, USER_ID)).willReturn(List.of(FILE_METADATA));

		fileService.findChildBy(PARENT_ID, USER_ID);

		then(fileDataAccessService).should(times(1)).findChildBy(PARENT_ID, USER_ID);
	}
}
