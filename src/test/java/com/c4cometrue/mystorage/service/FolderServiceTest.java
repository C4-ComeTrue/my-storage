package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.file.Path;
import java.util.LinkedList;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.c4cometrue.mystorage.dto.request.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.UpdateFolderNameReq;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.util.FileUtil;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
	@InjectMocks
	FolderService folderService;

	@Mock
	FolderRepository folderRepository;
	@Mock
	FileRepository fileRepository;
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
	@DisplayName("폴더 정보 조회 성공")
	void getFolderData() {
		// given
		// 1. 폴더 정보
		var folderName = "my_folder";
		var getFolderReq = new GetFolderReq(2L, folderName, mockUserName, 1L);
		var mockFolderPath = Path.of(mockRootPath).resolve(mockUserName).resolve(folderName);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName(folderName)
			.folderPath(mockFolderPath.toString())
			.userName(mockUserName)
			.parentFolderId(1L)
			.build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mock(FolderMetaData.class))); // 부모 폴더
		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(getFolderReq.userName(),
			getFolderReq.folderName(), getFolderReq.parentFolderId()))
			.willReturn(Optional.of(mockFolderMetaData)); // 조회하려는 폴더 존재
		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 2L); // 폴더 pk 임의로 2로 설정

		// 2. 해당 폴더에 하위 파일 1개, 하위 폴더 1개 있다고 가정
		var fileList = new LinkedList<FileMetaData>();
		fileList.add(FileMetaData.builder()
			.fileName(mockFileName)
			.fileStorageName(mockFileStorageName)
			.userName(mockUserName)
			.size(mockSize)
			.mime(mockContentType)
			.folderId(2L)
			.build());

		var folderList = new LinkedList<FolderMetaData>();
		folderList.add(FolderMetaData.builder()
			.folderName("childFolder")
			.folderPath(mockFolderPath.resolve("childFolder").toString())
			.userName(mockUserName)
			.parentFolderId(2L)
			.build());

		given(fileRepository.findAllByFolderId(2L)).willReturn(Optional.of(fileList));
		given(folderRepository.findAllByParentFolderId(2L)).willReturn(Optional.of(folderList));

		// when
		var folderOverviewRes = folderService.getFolderData(getFolderReq);

		// then
		assertThat(folderOverviewRes)
			.matches(res -> StringUtils.equals(res.folderName(), folderName))
			.matches(res -> StringUtils.equals(res.userName(), mockUserName))
			.matches(res -> StringUtils.equals(res.folderList().get(0).folderName(), "childFolder"))
			.matches(res -> StringUtils.equals(res.fileList().get(0).fileStorageName(), mockFileStorageName));
	}

	@Test
	@DisplayName("폴더 정보 조회 실패 - 부모 폴더가 없음")
	void getFolderDataFail() {
		// given
		var folderName = "my_folder";
		var getFolderReq = new GetFolderReq(2L, folderName, mockUserName, 1L);
		given(folderRepository.findByFolderId(1L)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class, () -> folderService.getFolderData(getFolderReq));

		// then
		assertEquals(exception.getErrCode(), ErrorCd.FOLDER_NOT_EXIST.name());
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(folderRepository, times(0)).findByUserNameAndFolderNameAndParentFolderId(mockUserName, folderName,
			1L); // 조회하려던 폴더는 보려고 시도도 못 함
	}

	@Test
	@DisplayName("폴더 생성 성공")
	void createFolder() {
		// 폴더 데이터 정보 생성
		var folderName = "my_folder";
		var createFolderReq = new CreateFolderReq(folderName, mockUserName, 1L);
		var parentFolderData = FolderMetaData.builder().folderName(mockUserName).userName(mockUserName).folderPath(
			Path.of(mockRootPath).resolve(mockUserName).toString()).parentFolderId(0L).build();

		given(folderRepository.findByFolderId(createFolderReq.parentFolderId())).willReturn(
			Optional.of(parentFolderData)); // 부모 폴더 존재
		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(createFolderReq.userName(),
			createFolderReq.folderName(), createFolderReq.parentFolderId()))
			.willReturn(Optional.empty()); // 중복 폴더 없음
		given(storagePathService.createFolderPath(parentFolderData.getFolderPath(), folderName)).willReturn(
			Path.of(parentFolderData.getFolderPath()).resolve(folderName));

		// when
		var createFolderRes = folderService.createFolder(createFolderReq);

		// then
		assertThat(createFolderRes)
			.matches(res -> StringUtils.equals(res.folderName(), folderName))
			.matches(res -> StringUtils.equals(res.userName(), mockUserName));
	}

	@Test
	@DisplayName("폴더 생성 실패 - 동일한 위치, 유저, 이름으로 폴더 존재")
	void createFolderFail() {
		var folderName = "my_folder";
		var createFolderReq = new CreateFolderReq(folderName, mockUserName, 1L);
		given(folderRepository.findByFolderId(createFolderReq.parentFolderId())).willReturn(
			Optional.of(mock(FolderMetaData.class))); // 부모 폴더 존재
		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(createFolderReq.userName(),
			createFolderReq.folderName(), createFolderReq.parentFolderId()))
			.willReturn(Optional.of(mock(FolderMetaData.class)));

		// when
		var exception = assertThrows(ServiceException.class, () -> folderService.createFolder(createFolderReq));

		// then
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(folderRepository, times(1)).findByUserNameAndFolderNameAndParentFolderId(mockUserName, folderName, 1L);
		assertEquals(exception.getErrCode(), ErrorCd.DUPLICATE_FOLDER.name());
	}

	@Test
	@DisplayName("폴더 이름 수정 성공")
	void updateFolderName() {
		// given
		var mockNewFolderName = "random_folder";
		var updateFolderReq = new UpdateFolderNameReq("my_folder", mockUserName, mockNewFolderName, 1L);

		var parentFolderPath = Path.of(mockRootPath).resolve(mockUserName);
		var parentFolderData = FolderMetaData.builder().folderName(mockUserName).userName(mockUserName).folderPath(
			parentFolderPath.toString()).parentFolderId(0L).build();

		var folderPath = parentFolderPath.resolve("my_folder");
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.folderPath(folderPath.toString())
			.userName(mockUserName)
			.parentFolderId(1L)
			.build();

		ReflectionTestUtils.setField(parentFolderData, "folderId", 1L);
		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 2L);

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(parentFolderData)); // 부모 폴더 존재
		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(updateFolderReq.userName(),
			updateFolderReq.folderName(), updateFolderReq.parentFolderId()))
			.willReturn(Optional.of(mockFolderMetaData)); // 기존 폴더 존재

		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(
			updateFolderReq.userName(), updateFolderReq.newFolderName(), updateFolderReq.parentFolderId()
		)).willReturn(Optional.empty()); // 바꿀 폴더 중복 안됨

		// 이름 바꾼 폴더 경로
		var newFolderPath = parentFolderPath.resolve(mockNewFolderName);
		given(storagePathService.createFolderPath(parentFolderData.getFolderPath(), mockNewFolderName)).willReturn(
			newFolderPath);

		// 하위에 폴더 하나 있었다고 가정
		var folderList = new LinkedList<FolderMetaData>();
		var childFolder = FolderMetaData.builder()
			.folderName("child_folder")
			.folderPath(newFolderPath.resolve("childFolder").toString())
			.userName(mockUserName)
			.parentFolderId(2L)
			.build();
		folderList.add(childFolder);
		ReflectionTestUtils.setField(childFolder, "folderId", 3L);

		// 기존 폴더(my_folder이자 random_folder)의 PK는 2이라고 가정
		// 하위 폴더의 PK는 3이라고 가정
		given(folderRepository.findAllByParentFolderId(2L)).willReturn(Optional.of(folderList));
		given(folderRepository.findAllByParentFolderId(3L)).willReturn(Optional.empty());
		given(storagePathService.createFolderPath(newFolderPath.toString(),
			folderList.get(0).getFolderName())).willReturn(newFolderPath.resolve("childFolder"));

		// when
		folderService.updateFolderName(updateFolderReq);

		// then
		verify(folderRepository, times(1)).findByFolderId(1L);
	}

	@Test
	@DisplayName("폴더 이름 수정 실패 - 바꿀 이름이 같은 위치에 이미 존재함")
	void updateFolderNameFail() {
		// given
		var mockNewFolderName = "random_folder";
		var updateFolderReq = new UpdateFolderNameReq("my_folder", mockUserName, mockNewFolderName, 1L);
		var parentFolderPath = Path.of(mockRootPath).resolve(mockUserName);
		var parentFolderData = FolderMetaData.builder().folderName(mockUserName).userName(mockUserName).folderPath(
			parentFolderPath.toString()).parentFolderId(0L).build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(parentFolderData)); // 부모 폴더 존재
		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(updateFolderReq.userName(),
			updateFolderReq.folderName(), updateFolderReq.parentFolderId()))
			.willReturn(Optional.of(mock(FolderMetaData.class))); // 기존 폴더 존재

		given(folderRepository.findByUserNameAndFolderNameAndParentFolderId(
			updateFolderReq.userName(), updateFolderReq.newFolderName(), updateFolderReq.parentFolderId()
		)).willReturn(Optional.of(mock(FolderMetaData.class))); // 바꿀 폴더 중복 안됨

		// when
		var exception = assertThrows(ServiceException.class, () -> folderService.updateFolderName(updateFolderReq));

		// then
		assertEquals(exception.getErrCode(), ErrorCd.DUPLICATE_FOLDER.name());
		verify(folderRepository, times(1)).findByFolderId(1L);
		verify(folderRepository, times(1)).findByUserNameAndFolderNameAndParentFolderId(mockUserName, "my_folder", 1L);
		verify(folderRepository, times(1)).findByUserNameAndFolderNameAndParentFolderId(mockUserName, mockNewFolderName,
			1L);
	}
}
