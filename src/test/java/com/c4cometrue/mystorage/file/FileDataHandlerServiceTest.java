package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.TestConstants.*;
import static java.lang.Boolean.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.util.PagingUtil;

@DisplayName("파일 데이터 액세스 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileDataHandlerServiceTest {
	@InjectMocks
	private FileDataHandlerService fileDataHandlerService;
	@Mock
	FileRepository fileRepository;

	@Test
	@DisplayName("파일 삭제 테스트")
	void shouldDeleteByFileIdTest() {
		when(fileRepository.existsById(FILE_ID)).thenReturn(true);
		assertDoesNotThrow(() -> fileDataHandlerService.deleteBy(FILE_ID));
		verify(fileRepository, times(1)).deleteById(FILE_ID);
	}

	@Test
	@DisplayName("파일 삭제 테스트 : 실패")
	void shouldDeleteByFileIdFailTest() {
		when(fileRepository.existsById(FILE_ID)).thenReturn(false);
		assertThrows(ServiceException.class, () -> fileDataHandlerService.deleteBy(FILE_ID));
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void shouldFindByFileIdAndUserIdTest() {
		when(fileRepository.findByIdAndUploaderId(FILE_ID, USER_ID)).thenReturn(Optional.of(FILE_METADATA));

		fileDataHandlerService.findBy(FILE_ID, USER_ID);

		verify(fileRepository, times(1)).findByIdAndUploaderId(FILE_ID, USER_ID);
	}

	@Test
	@DisplayName("파일 조회 테스트 : 실패")
	void shouldThrowExceptionWhenFileNotFountTest() {
		when(fileRepository.findByIdAndUploaderId(FILE_ID, USER_ID)).thenReturn(Optional.empty());
		assertThrows(ServiceException.class, () -> fileDataHandlerService.findBy(FILE_ID, USER_ID));
	}

	@Test
	@DisplayName("파일 저장 테스트")
	void shouldPersistFile() {
		fileDataHandlerService.persist(FILE_METADATA);

		verify(fileRepository, times(1)).save(FILE_METADATA);
	}


	@Test
	@DisplayName("파일 리스트 조회 커서Id가 널일때")
	void getFileListCursorIdIsNullTest() {
		given(fileRepository.findAllByParentIdAndUploaderIdOrderByIdDesc(PARENT_ID, USER_ID, PagingUtil.createPageable(10)))
			.willReturn(List.of(FILE_METADATA));

		fileDataHandlerService.getFileList(PARENT_ID, null, USER_ID, PagingUtil.createPageable(10));

		then(fileRepository).should(times(1)).findAllByParentIdAndUploaderIdOrderByIdDesc(PARENT_ID, USER_ID, PagingUtil.createPageable(10));
	}

	@Test
	@DisplayName("파일 리스트 조회")
	void getFileListTest() {
		given(fileRepository.findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(PARENT_ID, FILE_ID, USER_ID, PagingUtil.createPageable(10)))
			.willReturn(List.of(FILE_METADATA));

		fileDataHandlerService.getFileList(PARENT_ID, FILE_ID, USER_ID, PagingUtil.createPageable(10));

		then(fileRepository).should(times(1)).findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(PARENT_ID, FILE_ID, USER_ID, PagingUtil.createPageable(10));
	}

	@Test
	@DisplayName("다음 볼 파일이 있는 지 확인")
	void hashNextTest() {
		given(fileRepository.existsByParentIdAndUploaderIdAndIdLessThan(PARENT_ID, USER_ID, FILE_ID))
			.willReturn(FALSE);

		fileDataHandlerService.hashNext(PARENT_ID, USER_ID, FILE_ID);

		then(fileRepository).should(times(1)).existsByParentIdAndUploaderIdAndIdLessThan(PARENT_ID, USER_ID, FILE_ID);
	}

	@Test
	@DisplayName("특정 폴더 내 파일 중복 테스트")
	void duplicateTest() {
		given(fileRepository.checkDuplicateFileName(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME)).willReturn(false);
		fileDataHandlerService.duplicateBy(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME);
		then(fileRepository).should(times(1)).checkDuplicateFileName(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME);
	}

	@Test
	@DisplayName("특정 폴더 내 파일 중복 테스트 실패")
	void duplicateFailTest() {
		given(fileRepository.checkDuplicateFileName(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME)).willReturn(true);

		assertThrows(ServiceException.class, () -> fileDataHandlerService.duplicateBy(PARENT_ID, USER_ID, ORIGINAL_FILE_NAME));
	}
}
