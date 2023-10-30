package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;

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
class FolderDataAccessServiceTest {
	@InjectMocks
	private FolderDataAccessService folderDataAccessService;

	@Mock
	private FolderRepository folderRepository;

	@Test
	@DisplayName("폴더 메타 데이터 찾기")
	void findBy() {
		given(folderRepository.findById(PARENT_ID)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataAccessService.findBy(PARENT_ID);

		verify(folderRepository, times(1)).findById(PARENT_ID);
	}

	@Test
	@DisplayName("폴더 메타 데이터 찾기 : 실패")
	void findByFail() {
		given(folderRepository.findById(PARENT_ID)).willReturn(Optional.empty());

		assertThrows(ServiceException.class, () -> folderDataAccessService.findBy(PARENT_ID));
	}

	@Test
	@DisplayName("부모 폴더 기반 경로 찾기")
	void findPathBy() {
		given(folderRepository.findById(PARENT_ID)).willReturn(Optional.of(FOLDER_METADATA));

		folderDataAccessService.findPathBy(PARENT_ID);

		verify(folderRepository, times(1)).findById(PARENT_ID);
	}

	@Test
	@DisplayName("폴더 경로 찾기 : 부모id 가 null")
	void findPathParentIsNull() {
		folderDataAccessService.findPathBy(null);
		verify(folderRepository, times(0)).findById(null);
	}

	@Test
	@DisplayName("유효성 검사 부모id, 유저id 를 가지는 파일 : 부모id 가 널")
	void verifyParentIsNull() {
		folderDataAccessService.verifyBy(null, PARENT_ID);

		verify(folderRepository, times(0)).existsByParentIdAndUserId(null, PARENT_ID);
	}

	@Test
	@DisplayName("유효성 검사 부모id, 유저id 를 가지는 파일")
	void verifyBy() {
		given(folderRepository.existsByParentIdAndUserId(PARENT_ID, USER_ID)).willReturn(Boolean.TRUE);

		folderDataAccessService.verifyBy(PARENT_ID, USER_ID);

		verify(folderRepository, times(1)).existsByParentIdAndUserId(PARENT_ID, USER_ID);

	}

	@Test
	@DisplayName("유효성 검사 부모id, 유저id 를 가지는 파일 : 실패")
	void verifyByFalse() {
		given(folderRepository.existsByParentIdAndUserId(PARENT_ID, USER_ID)).willReturn(Boolean.FALSE);

		assertThrows(ServiceException.class, () -> folderDataAccessService.verifyBy(PARENT_ID, USER_ID));
	}

	@Test
	@DisplayName("폴더 중복 생성 검사")
	void checkDuplicateBy() {
		given(folderRepository.existsByParentIdAndUserIdAndOriginalFolderName(PARENT_ID, USER_ID,
			USER_FOLDER_NAME)).willReturn(Boolean.FALSE);

		folderDataAccessService.checkDuplicateBy(USER_FOLDER_NAME, PARENT_ID, USER_ID);

		verify(folderRepository, times(1)).existsByParentIdAndUserIdAndOriginalFolderName(PARENT_ID, USER_ID,
			USER_FOLDER_NAME);
	}

	@Test
	@DisplayName("폴더 중복 생성 검사 : 실패")
	void checkDuplicateFail() {
		given(folderRepository.existsByParentIdAndUserIdAndOriginalFolderName(PARENT_ID, USER_ID,
			USER_FOLDER_NAME)).willReturn(Boolean.TRUE);

		assertThrows(ServiceException.class,
			() -> folderDataAccessService.checkDuplicateBy(USER_FOLDER_NAME, PARENT_ID, USER_ID));
	}

	@Test
	@DisplayName("폴더 메타데이터 저장")
	void persist() {
		given(folderRepository.existsByParentIdAndUserId(PARENT_ID, USER_ID)).willReturn(Boolean.TRUE);
		given(folderRepository.existsByParentIdAndUserIdAndOriginalFolderName(PARENT_ID, USER_ID,
			USER_FOLDER_NAME)).willReturn(Boolean.FALSE);

		folderDataAccessService.persist(USER_FOLDER_NAME, STORED_FOLDER_NAME, FOLDER_PATH.toString(), USER_ID,
			PARENT_ID);

		verify(folderRepository, times(1)).save(any());
	}

}
