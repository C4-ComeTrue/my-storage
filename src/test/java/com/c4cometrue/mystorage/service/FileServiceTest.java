package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
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

import com.c4cometrue.mystorage.dto.request.FileReq;
import com.c4cometrue.mystorage.dto.request.UploadFileReq;
import com.c4cometrue.mystorage.entity.FileMetaData;
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
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(
			Optional.of(mockRootPath + "/" + mockUserName));
		given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
		given(mockMultipartFile.getSize()).willReturn(mockSize);
		given(mockMultipartFile.getContentType()).willReturn(mockContentType);
		var uploadFileReq = new UploadFileReq(mockMultipartFile, mockUserName, 1L);

		// when
		var createFileRes = fileService.uploadFile(uploadFileReq);

		// then
		assertThat(createFileRes)
			.matches(metadata -> StringUtils.equals(
				StringUtils.substring(metadata.fileStorageName(), 36), mockFileName))
			.matches(metadata -> metadata.size() == mockSize)
			.matches(metadata -> StringUtils.equals(metadata.mime(), mockContentType))
			.matches(metadata -> StringUtils.equals(metadata.userName(), mockUserName));
	}

	@Test
	@DisplayName("파일 업로드 실패 - 중복 파일명")
	void uploadFileFailDuplicateName() {
		// given
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(
			Optional.of(mockRootPath + "/" + mockUserName));
		given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
		given(fileRepository.findByFileNameAndUserNameAndFolderId(mockFileName, mockUserName, 1L))
			.willReturn(Optional.of(new FileMetaData()));
		var uploadFileReq = new UploadFileReq(mockMultipartFile, mockUserName, 1L);

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.uploadFile(uploadFileReq));

		// then
		assertEquals(ErrorCd.DUPLICATE_FILE.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 데이터 DB 확인")
	void getFileMetaData() {
		// given
		var mockFileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(1L)
			.build();
		given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

		// when
		var fileMetadata = fileService.getFileMetaData(mockFileStorageName, mockUserName);

		// then
		assertThat(fileMetadata)
			.matches(metadata -> StringUtils.equals(metadata.getFileName(), mockFileName))
			.matches(metadata -> metadata.getSize() == mockSize)
			.matches(metadata -> StringUtils.equals(metadata.getMime(), mockContentType))
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), mockUserName));
	}

	@Test
	@DisplayName("파일 데이터 DB 확인 실패 - 파일 없음")
	void getFileMetaDataFailWrongFileStorageName() {
		// given
		var wrongFileStorageName = "wrong_file_path.txt";
		given(fileRepository.findByFileStorageName(wrongFileStorageName)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.getFileMetaData(wrongFileStorageName, mockUserName));

		// then
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 데이터 DB 확인 실패 - 요청자가 주인이 아님")
	void getFileMetaDataFailNotOwner() {
		// given
		var mockFileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(1L)
			.build();
		given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.getFileMetaData(mockFileStorageName, "anonymous"));

		// then
		assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일 삭제")
	void deleteFile() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);
		var folderPath = Path.of(mockRootPath).resolve(mockUserName);
		var mockFileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(1L)
			.build();
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(
			Optional.of(folderPath.toString()));
		given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

		// when
		fileService.deleteFile(mockFileReq);

		// then
		verify(folderRepository, times(1)).findFolderPathByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
		verify(fileRepository, times(1)).delete(any());
	}

	@Test
	@DisplayName("파일 다운로드")
	void downloadFile() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);
		var mockResource = mock(Resource.class);
		var folderPath = Path.of(mockRootPath).resolve(mockUserName);
		var mockFileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(1L)
			.build();

		given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(
			Optional.of(folderPath.toString()));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(mockResource.exists()).willReturn(true);

		// when
		fileService.downloadFile(mockFileReq);

		// then
		verify(folderRepository, times(1)).findFolderPathByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
	}

	@Test
	@DisplayName("파일 다운로드 실패")
	void downloadFileFail() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);
		var mockResource = mock(Resource.class);
		var folderPath = Path.of(mockRootPath).resolve(mockUserName);
		var mockFileMetaData = FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(1L)
			.build();

		given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(
			Optional.of(folderPath.toString()));
		given(mockResourceLoader.getResource(any())).willReturn(mockResource);
		given(mockResource.exists()).willReturn(false);  // 물리적 파일을 찾지 못함

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(mockFileReq));

		// then
		verify(folderRepository, times(1)).findFolderPathByFolderId(1L);
		verify(fileRepository, times(1)).findByFileStorageName(any());
		assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
	}

	@Test
	@DisplayName("파일이 있어야할 폴더가 없다")
	void folderNotExist() {
		// given
		var mockFileReq = new FileReq(mockFileStorageName, mockUserName, 1L);
		given(folderRepository.findFolderPathByFolderId(1L)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> fileService.downloadFile(mockFileReq));

		// then
		assertEquals(ErrorCd.FOLDER_NOT_EXIST.name(), exception.getErrCode());
	}

}
