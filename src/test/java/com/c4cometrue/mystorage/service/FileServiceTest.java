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

import com.c4cometrue.mystorage.dto.request.file.FileReq;
import com.c4cometrue.mystorage.dto.request.file.UploadFileReq;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
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
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolderMetaData));
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));

		given(MOCK_MULTIPART_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(MOCK_MULTIPART_FILE.getSize()).willReturn(MOCK_SIZE);
		given(MOCK_MULTIPART_FILE.getContentType()).willReturn(MOCK_CONTENT_TYPE);

		var req = new UploadFileReq(MOCK_MULTIPART_FILE, MOCK_USER_NAME, 1L);

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
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("folderName")
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolderMetaData));
		given(MOCK_MULTIPART_FILE.getOriginalFilename()).willReturn(MOCK_FILE_NAME);
		given(fileRepository.findByFolderIdAndUserNameAndFileName(1L, MOCK_USER_NAME, MOCK_FILE_NAME))
			.willReturn(Optional.of(new FileMetaData()));
		var req = new UploadFileReq(MOCK_MULTIPART_FILE, MOCK_USER_NAME, 1L);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.uploadFile(MOCK_MULTIPART_FILE, MOCK_USER_NAME, 1L));

		// then
		assertEquals(ErrorCd.DUPLICATE_FILE.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 데이터 DB 확인")
	void getFileMetaData() {
		// given
		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(1L)
			.build();
		given(fileRepository.findByFileStorageName(MOCK_FILE_STORAGE_NAME)).willReturn(Optional.of(mockFileMetaData));

		// when
		var fileMetadata = fileService.getFileMetaData(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME);

		// then
		assertThat(fileMetadata)
			.matches(metadata -> StringUtils.equals(metadata.getFileName(), MOCK_FILE_NAME))
			.matches(metadata -> metadata.getSize() == MOCK_SIZE)
			.matches(metadata -> StringUtils.equals(metadata.getMime(), MOCK_CONTENT_TYPE))
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), MOCK_USER_NAME));
	}

	@Test
	@DisplayName("파일 데이터 DB 확인 실패 - 파일 없음")
	void getFileMetaDataFailWrongFileStorageName() {
		// given
		var wrongFileStorageName = "wrong_file_path.txt";
		given(fileRepository.findByFileStorageName(wrongFileStorageName)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.getFileMetaData(wrongFileStorageName, MOCK_USER_NAME));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 데이터 DB 확인 실패 - 요청자가 주인이 아님")
	void getFileMetaDataFailNotOwner() {
		// given
		var mockFileMetaData = FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(1L)
			.build();
		given(fileRepository.findByFileStorageName(MOCK_FILE_STORAGE_NAME)).willReturn(Optional.of(mockFileMetaData));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.getFileMetaData(MOCK_FILE_STORAGE_NAME, "anonymous"));

		// then
		assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		var req = new FileReq(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L);
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
			.parentFolderId(1L)
			.build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolderMetaData));
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));
		given(fileRepository.findByFileStorageName(MOCK_FILE_STORAGE_NAME)).willReturn(Optional.of(mockFileMetaData));

		// when
		fileService.deleteFile(req.fileStorageName(), req.userName(), req.folderId());

		// then
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
		verify(fileRepository, times(1)).delete(any());
	}

	@Test
	@DisplayName("파일 다운로드")
	void downloadFile() {
		// given
		var req = new FileReq(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L);
		var mockResource = mock(Resource.class);
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
			.parentFolderId(1L)
			.build();

		given(fileRepository.findByFileStorageName(MOCK_FILE_STORAGE_NAME)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolderMetaData));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));

		given(mockResource.exists()).willReturn(true);

		// when
		fileService.downloadFile(req.fileStorageName(), req.userName(), req.folderId());

		// then
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
	}

	@Test
	@DisplayName("파일 다운로드 실패")
	void downloadFileFail() {
		// given
		var req = new FileReq(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L);
		var mockResource = mock(Resource.class);
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

		given(fileRepository.findByFileStorageName(MOCK_FILE_STORAGE_NAME)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolderMetaData));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(storagePathService.createPathByUser(MOCK_USER_NAME)).willReturn(
			Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME));
		// 물리적 파일을 찾지 못함
		given(mockResource.exists()).willReturn(false);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L));

		// then
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일이 있어야할 폴더가 없다")
	void folderNotExist() {
		// given
		var req = new FileReq(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L);
		given(folderRepository.findByFolderId(1L)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(MOCK_FILE_STORAGE_NAME, MOCK_USER_NAME, 1L));

		// then
		assertEquals(ErrorCd.FOLDER_NOT_EXIST.name(), exception.getErrCode());
	}

}
