package com.c4cometrue.mystorage.file.util;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {

	private static MockedStatic<Files> filesMockedStatic;

	@BeforeAll // 매번 테스트 시작 전 초기화 작업
	public static void setup() {
		filesMockedStatic = mockStatic(Files.class);
	}

	@AfterAll
	public static void tearDown() {
		filesMockedStatic.close();
	}

	@Test
	@DisplayName("파일 업로드 성공")
	void fileUploadSuccessTest() throws IOException {
		// given
		var inputStream = mock(InputStream.class);

		given(MOCK_MULTIPLE_FILE.isEmpty()).willReturn(false);
		given(MOCK_MULTIPLE_FILE.getInputStream()).willReturn(inputStream);

		// when
		FileUtil.fileUpload(MOCK_MULTIPLE_FILE, MOCK_UPLOAD_PATH);

		// then
		filesMockedStatic.verify(() -> Files.copy(inputStream, MOCK_UPLOAD_PATH), times(1));
	}

	@Test
	@DisplayName("파일 업로드 실패: 빈 파일")
	void fileUploadFailEmptyTest() {

		given(MOCK_MULTIPLE_FILE.isEmpty()).willReturn(true);

		var emptyException = assertThrows(ServiceException.class,
			() -> FileUtil.fileUpload(MOCK_MULTIPLE_FILE, MOCK_UPLOAD_PATH));

		assertEquals(ErrorCode.FILE_BAD_REQUEST.name(), emptyException.getErrorCode());
	}

	@Test
	@DisplayName("파일 업로드 실패 : 업로드 진행 중 실패")
	void fileUploadFailIOTest() throws IOException {

		given(MOCK_MULTIPLE_FILE.isEmpty()).willReturn(false);
		given(Files.copy(MOCK_MULTIPLE_FILE.getInputStream(), MOCK_UPLOAD_PATH))
			.willThrow(IOException.class);

		var exception = assertThrows(ServiceException.class,
			() -> FileUtil.fileUpload(MOCK_MULTIPLE_FILE, MOCK_UPLOAD_PATH));

		assertEquals(ErrorCode.FILE_SERVER_ERROR.name(), exception.getErrorCode());
	}

	@Test
	@DisplayName("파일 다운로드 실패 : 다운로드할 ")
	void fileDownloadFailTest() throws IOException {
		InputStream is = mock(InputStream.class);
		OutputStream os = mock(OutputStream.class);

		given(Files.newInputStream(MOCK_UPLOAD_PATH)).willThrow(new IOException());
		given(Files.newOutputStream(MOCK_DOWNLOAD_PATH)).willThrow(new IOException());

		var exception = assertThrows(ServiceException.class,
			() -> FileUtil.fileDownload(MOCK_UPLOAD_PATH, MOCK_DOWNLOAD_PATH, MOCK_READ_CNT, MOCK_BUFFER));

		assertEquals(ErrorCode.FILE_SERVER_ERROR.name(), exception.getErrorCode());
	}

	@Test
	@DisplayName("파일 삭제 실패: 파일이 없는 경우")
	void fileDeleteFailEmptyTest() {
		given(Files.exists(MOCK_DELETE_PATH)).willReturn(false);

		var exception = assertThrows(ServiceException.class, () -> FileUtil.fileDelete(MOCK_DELETE_PATH));
		assertEquals(ErrorCode.FILE_NOT_EXIST.name(), exception.getErrorCode());
	}

	@Test
	@DisplayName("파일 삭제 실패 : 삭제 진행 중 실패")
	void fileDeleteFailProcessTest() {
		given(Files.exists(MOCK_DELETE_PATH)).willReturn(true);

		FileUtil.fileDelete(MOCK_DELETE_PATH);

		filesMockedStatic.verify(() -> Files.delete(MOCK_DELETE_PATH), times(1));
	}
}
