package com.c4cometrue.mystorage.file.service;

import static com.c4cometrue.mystorage.file.TestMockFile.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.file.entity.FileMetaData;
import com.c4cometrue.mystorage.file.repository.FileRepository;
import com.c4cometrue.mystorage.file.util.FileUtil;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

	@InjectMocks
	FileService fileService;

	@Mock
	FileRepository fileRepository;

	@Mock
	FileUtil fileUtil;

	@Mock
	FilePathService filePathService;

	private static MockedStatic<FileUtil> fileUtilMockedStatic;

	@BeforeAll // 매번 테스트 시작 전 초기화 작업
	public static void init() {
		fileUtilMockedStatic = mockStatic(FileUtil.class);
	}

	@AfterAll
	public static void down() {
		fileUtilMockedStatic.close();
	}

	@DisplayName("파일업로드 실패 테스트")
	@Test
	void fileUploadFailTest() {
		given(MOCK_MULTIPLE_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(fileRepository.findByFileNameAndUserName(MOCK_FILE_NAME, MOCK_USER_NAME))
			.willReturn(Optional.of(new FileMetaData()));

		ServiceException se = assertThrows(ServiceException.class,
			() -> fileService.fileUpload(MOCK_MULTIPLE_FILE, MOCK_USER_NAME));

		assertEquals(ErrorCode.FILE_IS_DUPLICATED.name(), se.getErrorCode());
	}

	@DisplayName("파일업로드 성공 테스트")
	@Test
	void fileUploadSuccessTest() {

		given(MOCK_MULTIPLE_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(MOCK_MULTIPLE_FILE.getSize()).willReturn(MOCK_SIZE);
		given(MOCK_MULTIPLE_FILE.getContentType()).willReturn(MOCK_CONTENT_TYPE);

		// when
		fileService.fileUpload(MOCK_MULTIPLE_FILE, MOCK_USER_NAME);

		// then
		assertThat(MOCK_FILE_META_DATA)
			.matches(metadata -> StringUtils.equals(metadata.getFileName(), MOCK_FILE_NAME))
			.matches(metadata -> metadata.getFileSize() == MOCK_SIZE)
			.matches(metadata -> StringUtils.equals(metadata.getFileMine(), MOCK_CONTENT_TYPE)
			);
	}

	@DisplayName("파일다운로드 실패")
	@Test
	void fileDownloadFailTest() throws IOException {
		// given
		// var inputStream = mock(InputStream.class);
		var outputStream = mock(OutputStream.class);
		var files = mockStatic(Files.class);

		var otherUserName = "otherUser";
		var otherFileName = "otherFileName";

		given(fileRepository.findByFileName(MOCK_FILE_NAME)).willReturn(Optional.of(MOCK_FILE_META_DATA));
		given(Files.newOutputStream(MOCK_DOWNLOAD_PATH)).willReturn(outputStream);

		var permissionException = assertThrows(ServiceException.class,
			() -> fileService.fileDownload(MOCK_FILE_NAME, otherUserName, MOCK_DOWN_ROOT_PATH));

		var notFoundException = assertThrows(ServiceException.class,
			() -> fileService.fileDownload(otherFileName, MOCK_USER_NAME, MOCK_DOWN_ROOT_PATH));

		assertEquals(ErrorCode.FILE_PERMISSION_DENIED.name(), permissionException.getErrorCode());
		assertEquals(ErrorCode.FILE_NOT_EXIST.name(), notFoundException.getErrorCode());
		files.close();
	}

	@DisplayName("파일 다운로드 성공")
	@Test
	void fileDownloadSuccessTest() throws IOException {
		// given
		var inputStream = mock(InputStream.class);
		var outputStream = mock(OutputStream.class);
		var files = mockStatic(Files.class);

		given(fileRepository.findByFileName(MOCK_FILE_NAME)).willReturn(Optional.of(MOCK_FILE_META_DATA));
		given(Files.newInputStream(MOCK_UPLOAD_PATH)).willReturn(inputStream);
		given(Files.newOutputStream(MOCK_DOWNLOAD_PATH)).willReturn(outputStream);

		fileService.fileDownload(MOCK_FILE_NAME, MOCK_USER_NAME, MOCK_DOWN_ROOT_PATH);

		verify(fileRepository, times(1)).findByFileName(any());

		files.close();
	}

	@DisplayName("파일삭제 성공 테스트")
	@Test
	void fileDeleteSuccessTest() {
		// given
		given(fileRepository.findByFileName(MOCK_FILE_NAME)).willReturn(Optional.of(MOCK_FILE_META_DATA));

		// when
		fileService.fileDelete(MOCK_FILE_META_DATA.getFileName(), MOCK_FILE_META_DATA.getUserName());

		// then
		verify(fileRepository, times(1)).delete(any());
	}

	@DisplayName("파일삭제 실패 테스트")
	@Test
	void fileDeleteFailTest() {
		// given
		var otherUserName = "otherUserName";
		var otherFileName = "otherFileName";

		given(fileRepository.findByFileName(MOCK_FILE_NAME)).willReturn(Optional.of(MOCK_FILE_META_DATA));

		// when
		var permissionException = assertThrows(ServiceException.class,
			() -> fileService.fileDelete(
				MOCK_FILE_NAME,
				otherUserName
			));

		var notFoundException = assertThrows(ServiceException.class,
			() -> fileService.fileDelete(
				otherFileName,
				MOCK_USER_NAME
			));

		// then
		assertEquals(ErrorCode.FILE_PERMISSION_DENIED.name(), permissionException.getErrorCode());
		assertEquals(ErrorCode.FILE_NOT_EXIST.name(), notFoundException.getErrorCode());
	}
}
