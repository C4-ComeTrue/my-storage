package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.file.Paths;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.c4cometrue.mystorage.dto.request.file.UploadFileReq;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.repository.DeleteLogRepository;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.util.FileUtil;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
	@InjectMocks
	FileService fileService;
	@Mock
	FileRepository fileRepository;
	@Mock
	FolderRepository folderRepository;
	@Mock
	ResourceLoader mockResourceLoader;
	@Mock
	StoragePathService storagePathService;
	@Mock
	DeleteLogRepository deleteLogRepository;

	private static MockedStatic<FileUtil> fileUtilMockedStatic;

	@BeforeAll
	public static void setup() {
		fileUtilMockedStatic = mockStatic(FileUtil.class);
	}

	@AfterAll
	public static void tearDown() {
		fileUtilMockedStatic.close();
	}

	@Test
	@DisplayName("파일 업로드 성공")
	void uploadFile() {
		// given
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));

		given(MOCK_MULTIPART_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(MOCK_MULTIPART_FILE.getSize()).willReturn(MOCK_SIZE);
		given(MOCK_MULTIPART_FILE.getContentType()).willReturn(MOCK_CONTENT_TYPE);

		var req = new UploadFileReq(MOCK_MULTIPART_FILE, MOCK_USER_NAME, folderId);

		// when
		var createFileRes = fileService.uploadFile(req.file(), req.userName(), req.folderId());

		// then
		assertThat(createFileRes)
			.matches(metadata -> StringUtils.equals(
				StringUtils.substring(metadata.fileStorageName(), 36), MOCK_FILE_NAME))
			.matches(metadata -> metadata.size() == MOCK_SIZE)
			.matches(metadata -> StringUtils.equals(metadata.mime(), MOCK_CONTENT_TYPE))
			.matches(metadata -> StringUtils.equals(metadata.userName(), MOCK_USER_NAME));
	}

	@Test
	@DisplayName("파일 업로드 실패 - 중복 파일명")
	void uploadFileFailDuplicateName() {
		// given
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(folderId)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(MOCK_MULTIPART_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(fileRepository.findByFolderIdAndUserNameAndFileName(folderId, MOCK_USER_NAME, MOCK_FILE_NAME))
			.willReturn(Optional.of(new FileMetaData()));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.uploadFile(MOCK_MULTIPART_FILE, MOCK_USER_NAME, folderId));

		// then
		assertEquals(ErrorCd.DUPLICATE_FILE.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(folderId)
			.build();
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(fileRepository.findById(fileId)).willReturn(Optional.of(mockFileMetaData));

		// when
		fileService.deleteFile(fileId, MOCK_USER_NAME, folderId);

		// then
		verify(folderRepository, times(1)).findByFolderId(folderId);
		verify(fileRepository, times(1)).findById(fileId);
		verify(fileRepository, times(1)).delete(mockFileMetaData);
		verify(deleteLogRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("파일 삭제 실패 - 파일 데이터 조회 실패")
	void deleteFileFailByFileMetaDataNotFound() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(fileRepository.findById(fileId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.deleteFile(fileId, MOCK_USER_NAME, folderId));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(folderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 삭제 실패 - 권한이 없음")
	void deleteFileFailByNoAuthority() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();
		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.deleteFile(fileId, "anonymous", folderId));

		// then
		assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(folderId);
	}

	@Test
	@DisplayName("파일 다운로드")
	void downloadFile() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockResource = mock(Resource.class);
		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(folderId)
			.build();
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(folderId)
			.build();

		given(fileRepository.findById(fileId)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));

		given(mockResource.exists()).willReturn(true);

		// when
		fileService.downloadFile(fileId, MOCK_USER_NAME, folderId);

		// then
		verify(folderRepository, times(1)).findByFolderId(folderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 다운로드 실패 - 파일 데이터 조회 실패")
	void downloadFileFailByFileMetaDataNotFound() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(fileRepository.findById(fileId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(fileId, MOCK_USER_NAME, folderId));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(folderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 다운로드 실패 - 권한이 없음")
	void downloadFileFailByNoAuthority() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(fileId, "anonymous", folderId));

		// then
		assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(folderId);
	}


	@Test
	@DisplayName("파일 다운로드 실패")
	void downloadFileFailByFileNotExist() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		var mockResource = mock(Resource.class);
		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(folderId)
			.build();

		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(fileRepository.findById(fileId)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findByFolderId(folderId)).willReturn(Optional.of(mockFolderMetaData));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));
		// 물리적 파일을 찾지 못함
		given(mockResource.exists()).willReturn(false);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(fileId, MOCK_USER_NAME, folderId));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(folderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일이 있어야할 폴더가 없다")
	void folderNotExist() {
		// given
		var fileId = 1L;
		var folderId = 1L;
		given(folderRepository.findByFolderId(fileId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(fileId, MOCK_USER_NAME, folderId));

		// then
		assertEquals(ErrorCd.FOLDER_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 이동 성공")
	void moveFile() {
		// given
		var fileId = 1L;
		var targetFolderId = 1234L;

		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(1L)
			.build();
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(fileRepository.findById(fileId)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findByFolderId(targetFolderId)).willReturn(Optional.of(mockFolderMetaData));

		// when
		fileService.moveFile(fileId, targetFolderId, MOCK_USER_NAME);

		// then
		assertEquals(mockFileMetaData.getFolderId(), targetFolderId);
		verify(fileRepository, times(1)).findById(fileId);
		verify(folderRepository, times(1)).findByFolderId(targetFolderId);
	}

	@Test
	@DisplayName("파일 이동 실패 - 파일 데이터 조회 실패")
	void moveFileFailByFileMetaDataNotFound() {
		// given
		var fileId = 1L;
		var targetFolderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(targetFolderId)).willReturn(Optional.of(mockFolderMetaData));
		given(fileRepository.findById(fileId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.moveFile(fileId, targetFolderId, MOCK_USER_NAME));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(targetFolderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 이동 실패 - 권한이 없음")
	void moveFileFailByNoAuthority() {
		// given
		var fileId = 1L;
		var targetFolderId = 1L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(targetFolderId)).willReturn(Optional.of(mockFolderMetaData));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.moveFile(fileId, targetFolderId, "anonymous"));

		// then
		assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(targetFolderId);
	}

	@Test
	@DisplayName("파일 이동 실패 - 파일 정보 없음")
	void moveFileFailByFileNotFound() {
		// given
		var fileId = 1L;
		var targetFolderId = 1234L;
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(targetFolderId)).willReturn(Optional.of(mockFolderMetaData));
		given(fileRepository.findById(fileId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.moveFile(fileId, targetFolderId, MOCK_USER_NAME));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(targetFolderId);
		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 이동 실패 - 이동할 폴더가 없음")
	void moveFileFailByFolderNotFound() {
		// given
		var fileId = 1L;
		var targetFolderId = 1234L;
		given(folderRepository.findByFolderId(targetFolderId)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.moveFile(fileId, targetFolderId, MOCK_USER_NAME));

		// then
		assertEquals(ErrorCd.FOLDER_NOT_EXIST.name(), exception.getErrCode());
		verify(folderRepository, times(1)).findByFolderId(targetFolderId);
	}

}
