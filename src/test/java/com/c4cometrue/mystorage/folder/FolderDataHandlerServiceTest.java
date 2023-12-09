package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;

import static java.lang.Boolean.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
@DisplayName("폴더 데이터 어세스 서비스 테스트")
class FolderDataHandlerServiceTest {
	@InjectMocks
	private FolderDataHandlerService folderDataHandlerService;

	@Mock
	private FolderRepository folderRepository;

	@Test
	@DisplayName("부모 폴더 기반 경로 찾기")
	void findPathBy() {
		given(folderRepository.findById(PARENT_ID)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataHandlerService.findPathBy(PARENT_ID);

		verify(folderRepository, times(1)).findById(PARENT_ID);
	}

	@Test
	@DisplayName("폴더 경로 찾기 : 부모id 가 null")
	void findPathParentIsNull() {
		folderDataHandlerService.findPathBy(null);
		verify(folderRepository, times(0)).findById(null);
	}

	@Test
	@DisplayName("폴더 메타데이터 저장")
	void persist() {
		given(folderRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(TRUE);
		given(folderRepository.existsByParentIdAndUploaderIdAndOriginalFolderName(PARENT_ID, USER_ID, USER_FOLDER_NAME))
			.willReturn(FALSE);

		folderDataHandlerService.persist(USER_FOLDER_NAME, STORED_FOLDER_NAME, FOLDER_PATH.toString(), USER_ID,
			PARENT_ID);

		verify(folderRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("폴더 메타데이터 저장 중복된 파일 : 실패")
	void persistFail() {
		given(folderRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(TRUE);
		given(folderRepository.existsByParentIdAndUploaderIdAndOriginalFolderName(PARENT_ID, USER_ID, USER_FOLDER_NAME))
			.willReturn(TRUE);

		assertThrows(ServiceException.class,
			() -> folderDataHandlerService.persist(USER_FOLDER_NAME, STORED_FOLDER_NAME, USER_PATH, USER_ID,
				PARENT_ID));
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트")
	void changeFolderTest() {
		given(folderRepository.existsByIdAndUploaderId(FOLDER_ID, USER_ID)).willReturn(TRUE);
		given(folderRepository.findById(FOLDER_ID)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataHandlerService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);

		verify(folderRepository, times(1)).existsByIdAndUploaderId(FOLDER_ID, USER_ID);
		verify(folderRepository, times(1)).save(FOLDER_METADATA);
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트 id 가 널")
	void changeFolderParentIdIsNullTest() {
		given(folderRepository.findById(null)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataHandlerService.changeFolderNameBy(USER_FOLDER_NAME, null, USER_ID);

		verify(folderRepository, times(1)).save(FOLDER_METADATA);
	}

	@Test
	@DisplayName("다른 유저 폴더명 변경 : 실패")
	void changeOtherUserFolderTest() {
		given(folderRepository.existsByIdAndUploaderId(FOLDER_ID, USER_ID)).willReturn(TRUE);

		assertThrows(ServiceException.class,
			() -> folderDataHandlerService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID));
	}

	@Test
	@DisplayName("루트 폴더 조회 테스트")
	void getChildFolder() {
		folderDataHandlerService.findChildBy(null, USER_ID);

		then(folderRepository).should(times(1)).findByParentIdAndUploaderId(null, USER_ID);
	}

	@Test
	@DisplayName("폴더 조회 테스트 : 실패")
	void getChildFolderFail() {
		given(folderRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(FALSE);

		assertThrows(ServiceException.class, () -> folderDataHandlerService.findChildBy(PARENT_ID, USER_ID));
	}
}
