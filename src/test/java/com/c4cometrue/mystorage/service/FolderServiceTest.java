package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.c4cometrue.mystorage.dto.request.folder.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.UpdateFolderNameReq;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
	@InjectMocks
	FolderService folderService;

	@Mock
	FolderRepository folderRepository;
	@Mock
	FileRepository fileRepository;

	@Test
	@DisplayName("폴더 정보 조회 성공")
	void getFolderData() {
		// given
		// 1. 폴더 정보
		var folderName = "my_folder";
		var req = new GetFolderReq(2L, MOCK_USER_NAME);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName(folderName)
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		// 조회하려는 폴더 존재
		given(folderRepository.findByFolderId(req.folderId())).willReturn(Optional.of(mockFolderMetaData));
		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 2L); // 폴더 pk 임의로 2로 설정

		// 2. 해당 폴더에 하위 파일 1개, 하위 폴더 1개 있다고 가정
		var fileList = new ArrayList<FileMetaData>();
		fileList.add(FileMetaData.builder()
			.fileName(MOCK_FILE_NAME)
			.fileStorageName(MOCK_FILE_STORAGE_NAME)
			.userName(MOCK_USER_NAME)
			.size(MOCK_SIZE)
			.mime(MOCK_CONTENT_TYPE)
			.folderId(2L)
			.build());

		var folderList = new ArrayList<FolderMetaData>();
		var subFolder = FolderMetaData.builder()
			.folderName("childFolder")
			.userName(MOCK_USER_NAME)
			.parentFolderId(2L)
			.build();
		ReflectionTestUtils.setField(subFolder, "folderId", 3L);
		folderList.add(subFolder);

		given(fileRepository.findAllByFolderId(2L)).willReturn(Optional.of(fileList));
		given(folderRepository.findAllByParentFolderId(2L)).willReturn(Optional.of(folderList));

		// when
		var folderOverviewRes = folderService.getFolderData(req.folderId(), req.userName());

		// then
		assertThat(folderOverviewRes)
			.matches(res -> StringUtils.equals(res.folderName(), folderName))
			.matches(res -> StringUtils.equals(res.userName(), MOCK_USER_NAME))
			.matches(res -> StringUtils.equals(res.folderList().get(0).folderName(), "childFolder"))
			.matches(res -> StringUtils.equals(res.fileList().get(0).fileStorageName(), MOCK_FILE_STORAGE_NAME));
	}

	@Test
	@DisplayName("폴더 정보 조회 실패 - 폴더가 없음")
	void getFolderDataFailNoFolder() {
		// given
		given(folderRepository.findByFolderId(1L)).willReturn(Optional.empty());

		// when
		var exception = assertThrows(ServiceException.class,
			() -> folderService.getFolderData(1L, MOCK_USER_NAME));

		// then
		assertEquals(exception.getErrCode(), ErrorCd.FOLDER_NOT_EXIST.name());
		verify(folderRepository, times(1)).findByFolderId(1L);
	}

	@Test
	@DisplayName("폴더 정보 조회 실패 - 권한이 없음")
	void getFolderDataFailNotOwner() {
		// given
		var folderName = "my_folder";
		var mockFolder = FolderMetaData.builder()
			.folderName(folderName)
			.userName(MOCK_USER_NAME)
			.parentFolderId(0L)
			.build();

		given(folderRepository.findByFolderId(1L)).willReturn(Optional.of(mockFolder));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> folderService.getFolderData(1L, "Anonymous"));

		// then
		assertEquals(exception.getErrCode(), ErrorCd.NO_PERMISSION.name());
		verify(folderRepository, times(1)).findByFolderId(1L);
	}

	@Test
	@DisplayName("폴더 생성 성공")
	void createFolder() {
		// 폴더 데이터 정보 생성
		var folderName = "my_folder";
		var req = new CreateFolderReq(1L, MOCK_USER_NAME, folderName);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName(folderName)
			.userName(MOCK_USER_NAME)
			.parentFolderId(10L)
			.build();

		// 중복 폴더 없음
		given(folderRepository.findByFolderNameAndParentFolderIdAndUserName(req.folderName(), req.parentFolderId(),
			req.userName()))
			.willReturn(Optional.empty());

		// 기본키 설정
		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 2L); // 폴더 pk 임의로 2로 설정
		given(folderRepository.save(any(FolderMetaData.class))).willReturn(mockFolderMetaData);

		// when
		var createFolderRes = folderService.createFolder(req.parentFolderId(), req.userName(), req.folderName());

		// then
		assertThat(createFolderRes)
			.matches(res -> StringUtils.equals(res.folderName(), folderName))
			.matches(res -> StringUtils.equals(res.userName(), MOCK_USER_NAME));
	}

	@Test
	@DisplayName("폴더 생성 실패 - 동일한 위치, 유저, 이름으로 폴더 존재")
	void createFolderFail() {
		var folderName = "my_folder";
		var req = new CreateFolderReq(1L, MOCK_USER_NAME, folderName);

		// 중복 폴더 존재
		given(folderRepository.findByFolderNameAndParentFolderIdAndUserName(
			req.folderName(), req.parentFolderId(), req.userName())).willReturn(
			Optional.of(mock(FolderMetaData.class)));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> folderService.createFolder(1L, MOCK_USER_NAME, folderName));

		// then
		verify(folderRepository, times(1)).findByFolderNameAndParentFolderIdAndUserName(folderName, 1L, MOCK_USER_NAME);
		assertEquals(exception.getErrCode(), ErrorCd.DUPLICATE_FOLDER.name());
	}

	@Test
	@DisplayName("폴더 이름 수정 성공")
	void updateFolderName() {
		// given
		var mockNewFolderName = "random_folder";
		var req = new UpdateFolderNameReq(2L, 1L, MOCK_USER_NAME, mockNewFolderName);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		ReflectionTestUtils.setField(mockFolderMetaData, "folderId", 2L);

		// 기존 폴더 존재
		given(folderRepository.findByFolderId(req.folderId())).willReturn(Optional.of(mockFolderMetaData));

		// 바꿀 폴더 중복 안됨
		given(folderRepository.findByFolderNameAndParentFolderIdAndUserName(
			mockNewFolderName, req.parentFolderId(), req.userName())).willReturn(Optional.empty());

		// when
		folderService.updateFolderName(req.folderId(), req.parentFolderId(), req.userName(), req.newFolderName());

		// then
		verify(folderRepository, times(1)).findByFolderId(2L);
		verify(folderRepository, times(1)).findByFolderNameAndParentFolderIdAndUserName(mockNewFolderName, 1L,
			MOCK_USER_NAME);
	}

	@Test
	@DisplayName("폴더 이름 수정 실패 - 바꿀 이름이 같은 위치에 이미 존재함")
	void updateFolderNameFail() {
		// given
		var mockNewFolderName = "random_folder";
		var req = new UpdateFolderNameReq(2L, 1L, MOCK_USER_NAME, mockNewFolderName);
		var mockFolderMetaData = FolderMetaData.builder()
			.folderName("my_folder")
			.userName(MOCK_USER_NAME)
			.parentFolderId(1L)
			.build();

		// 기존 폴더 존재
		given(folderRepository.findByFolderId(req.folderId())).willReturn(
			Optional.of(mockFolderMetaData));

		// 바꿀 폴더 중복
		given(folderRepository.findByFolderNameAndParentFolderIdAndUserName(
			req.newFolderName(), req.parentFolderId(), req.userName()
		)).willReturn(Optional.of(mock(FolderMetaData.class)));

		// when
		var exception = assertThrows(ServiceException.class,
			() -> folderService.updateFolderName(2L, 1L, MOCK_USER_NAME, mockNewFolderName));

		// then
		assertEquals(exception.getErrCode(), ErrorCd.DUPLICATE_FOLDER.name());
		verify(folderRepository, times(1)).findByFolderId(2L);
		verify(folderRepository, times(1)).findByFolderNameAndParentFolderIdAndUserName(mockNewFolderName, 1L,
			MOCK_USER_NAME);
	}
}
