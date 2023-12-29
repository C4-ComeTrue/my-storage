package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;

import static java.lang.Boolean.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.util.PagingUtil;

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
		given(folderRepository.findByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(
			Optional.ofNullable(FOLDER_METADATA));

		folderDataHandlerService.findPathBy(PARENT_ID, USER_ID);

		verify(folderRepository, times(1)).findByIdAndUploaderId(PARENT_ID, USER_ID);
	}

	@Test
	@DisplayName("폴더 경로 찾기 : 부모id 가 null")
	void findPathParentIsNull() {
		folderDataHandlerService.findPathBy(null, USER_ID);
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
		given(folderRepository.findByIdAndUploaderId(FOLDER_ID, USER_ID)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataHandlerService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);

		verify(folderRepository, times(1)).existsByIdAndUploaderId(FOLDER_ID, USER_ID);
		verify(folderRepository, times(1)).save(FOLDER_METADATA);
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트 id 가 널")
	void changeFolderParentIdIsNullTest() {
		given(folderRepository.findByIdAndUploaderId(null, USER_ID)).willReturn(Optional.of(FOLDER_METADATA));

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
	@DisplayName("폴더 리스트 조회 테스트 커서Id 가 널 일때")
	void getFolderListCursorIdIsNullTest() {
		given(folderRepository.findAllByParentIdAndUploaderIdOrderByIdDesc(PARENT_ID, USER_ID, PagingUtil.createPageable(10)))
			.willReturn(List.of(FOLDER_METADATA));

		List<FolderMetadata> result = folderDataHandlerService.getFolderList(PARENT_ID, null, USER_ID, PagingUtil.createPageable(10));

		assertNotNull(result);
		assertEquals(List.of(FOLDER_METADATA), result);

		then(folderRepository).should(times(1))
			.findAllByParentIdAndUploaderIdOrderByIdDesc(PARENT_ID, USER_ID, PagingUtil.createPageable(10));
	}

	@Test
	@DisplayName("폴더 리스트 조회 테스트")
	void getFolderListTest() {
		List<FolderMetadata> mockFolderList = List.of(FOLDER_METADATA); // Create a mock list of FolderMetadata
		given(folderRepository.findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(PARENT_ID, USER_ID, FOLDER_ID, PagingUtil.createPageable(10)))
			.willReturn(mockFolderList);

		List<FolderMetadata> result = folderDataHandlerService.getFolderList(PARENT_ID, FOLDER_ID, USER_ID, PagingUtil.createPageable(10));

		assertNotNull(result);
		assertEquals(mockFolderList, result);

		then(folderRepository).should(times(1))
			.findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(PARENT_ID, USER_ID, FOLDER_ID, PagingUtil.createPageable(10));
	}

	@Test
	@DisplayName("다음 폴더가 있는 지 테스트")
	void hasNextTest() {
		given(folderRepository.existsByParentIdAndUploaderIdAndIdLessThan(PARENT_ID, USER_ID, FOLDER_ID))
			.willReturn(FALSE);

		folderDataHandlerService.hasNext(PARENT_ID, USER_ID, FOLDER_ID);

		then(folderRepository).should(times(1))
			.existsByParentIdAndUploaderIdAndIdLessThan(PARENT_ID, USER_ID, FOLDER_ID);
	}

	@Test
	@DisplayName("폴더 유효성 검사 존재하지 않는 폴더 일때")
	void validateFolderOwnershipTest() {
		given(folderRepository.existsByIdAndUploaderId(FOLDER_ID, USER_ID)).willReturn(FALSE);

		assertThrows(ServiceException.class, () -> folderDataHandlerService.validateFolderOwnershipBy(FOLDER_ID, USER_ID));
	}

	@Test
	@DisplayName("폴더 유효성 검사 루트 폴더")
	void validateFolderOwnershipFolderIdIsNullTest() {
		assertDoesNotThrow(() -> {
			folderDataHandlerService.validateFolderOwnershipBy(null, USER_ID);
		});

		verify(folderRepository, never()).existsByIdAndUploaderId(any(), any());
	}

}
