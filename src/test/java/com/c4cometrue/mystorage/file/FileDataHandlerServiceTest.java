package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.TestConstants.*;
import static java.lang.Boolean.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("파일 데이터 액세스 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileDataHandlerServiceTest {
	@InjectMocks
	private FileDataHandlerService fileDataHandlerService;
	@Mock
	FileRepository fileRepository;

	@Test
	@DisplayName("파일 삭제 테스트")
	void shouldDeleteByFileId() {
		when(fileRepository.existsById(FILE_ID)).thenReturn(true);
		assertDoesNotThrow(() -> fileDataHandlerService.deleteBy(FILE_ID));
		verify(fileRepository, times(1)).deleteById(FILE_ID);
	}

	@Test
	@DisplayName("파일 삭제 테스트 : 실패")
	void shouldDeleteByFileIdFail() {
		when(fileRepository.existsById(FILE_ID)).thenReturn(false);
		assertThrows(ServiceException.class, () -> fileDataHandlerService.deleteBy(FILE_ID));
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void shouldFindByFileIdAndUserId() {
		when(fileRepository.findByIdAndUploaderId(FILE_ID, USER_ID)).thenReturn(Optional.of(FILE_METADATA));

		fileDataHandlerService.findBy(FILE_ID, USER_ID);

		verify(fileRepository, times(1)).findByIdAndUploaderId(FILE_ID, USER_ID);
	}

	@Test
	@DisplayName("파일 조회 테스트 : 실패")
	void shouldThrowExceptionWhenFileNotFount() {
		when(fileRepository.findByIdAndUploaderId(FILE_ID, USER_ID)).thenReturn(Optional.empty());
		assertThrows(ServiceException.class, () -> fileDataHandlerService.findBy(FILE_ID, USER_ID));
	}

	@Test
	@DisplayName("파일 저장 테스트")
	void shouldPersistFile() {
		given(fileRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(TRUE);
		given(fileRepository.checkDuplicateFileName(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME)).willReturn(FALSE);

		fileDataHandlerService.persist(FILE_METADATA, USER_ID, PARENT_ID);

		verify(fileRepository, times(1)).save(FILE_METADATA);
	}

	@Test
	@DisplayName("파일 저장 테스트 다른 유저 폴더 접근 : 실패")
	void shouldNotPersistFileInOtherFolder() {
		given(fileRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(FALSE);

		assertThrows(ServiceException.class, () -> fileDataHandlerService.persist(FILE_METADATA, USER_ID, PARENT_ID));
	}

	@Test
	@DisplayName("파일 저장 테스트 중복된 파일 생성 : 실패")
	void shouldNotPersistDuplicateFile() {
		given(fileRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(TRUE);
		given(fileRepository.checkDuplicateFileName(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME)).willReturn(TRUE);

		assertThrows(ServiceException.class, () -> fileDataHandlerService.persist(FILE_METADATA, USER_ID, PARENT_ID));
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void findChild() {
		given(fileRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(TRUE);

		fileDataHandlerService.findChildBy(PARENT_ID, USER_ID);

		verify(fileRepository, times(1)).existsByIdAndUploaderId(PARENT_ID, USER_ID);
	}


	@Test
	@DisplayName("파일 조회 테스트 : 실패")
	void findChildFail() {
		given(fileRepository.existsByIdAndUploaderId(PARENT_ID, USER_ID)).willReturn(FALSE);

		assertThrows(ServiceException.class, () -> fileDataHandlerService.findChildBy(PARENT_ID, USER_ID));
	}

	@Test
	@DisplayName("파일 조회 테스트 부모Id가 널일때")
	void findChildParentIdIsNull() {
		// given(fileRepository.existsByIdAndUploaderId(null, USER_ID)).willReturn(TRUE);
		given(fileRepository.findByParentIdAndUploaderId(null, USER_ID)).willReturn(new ArrayList<>());

		fileDataHandlerService.findChildBy(null, USER_ID);

		// verify(fileRepository, times(1)).existsByIdAndUploaderId(null, USER_ID);
		verify(fileRepository, times(1)).findByParentIdAndUploaderId(null, USER_ID);
	}
}
