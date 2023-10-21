package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
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

@DisplayName("파일 데이터 액세스 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileDataAccessServiceTest {
	@InjectMocks
	private FileDataAccessService fileDataAccessService;
	@Mock
	FileRepository fileRepository;

	@Test
	@DisplayName("파일 삭제 테스트")
	void shouldDeleteByFileId() {
		when(fileRepository.existsById(fileId)).thenReturn(true);
		assertDoesNotThrow(() -> fileDataAccessService.deleteBy(fileId));
		verify(fileRepository, times(1)).deleteById(fileId);
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void shouldFindByFileIdAndUserId() {
		when(fileRepository.findByIdAndUploaderId(fileId, userId)).thenReturn(Optional.of(METADATA));

		fileDataAccessService.findBy(fileId, userId);

		verify(fileRepository, times(1)).findByIdAndUploaderId(fileId, userId);
	}

	@Test
	@DisplayName("파일 조회 테스트 : 실패")
	void shouldThrowExceptionWhenFileNotFount() {
		when(fileRepository.findByIdAndUploaderId(fileId, userId)).thenReturn(Optional.empty());
		assertThrows(ServiceException.class, () -> fileDataAccessService.findBy(fileId, userId));
	}

	@Test
	@DisplayName("파일 저장 테스트")
	void shouldPersistFile() {
		fileDataAccessService.persist(METADATA, userId);

		verify(fileRepository, times(1)).save(METADATA);
	}

	@Test
	@DisplayName("존재하는 파일인지 테스트")
	void shouldExistByFile() {
		given(fileRepository.existsById(fileId)).willReturn(true);
		assertDoesNotThrow(() -> fileDataAccessService.existBy(fileId));
	}

	@Test
	@DisplayName("존재하는 파일 테스트 : 실패")
	void shouldThrowExceptionWhenFileNotExist() {
		given(fileRepository.existsById(fileId)).willReturn(false);
		assertThrows(ServiceException.class, () -> fileDataAccessService.existBy(fileId));
	}

	@Test
	@DisplayName("중복 파일 검사")
	void shouldNotDuplicateFile() {
		given(fileRepository.checkDuplicateFileName(OriginalFileName, userId)).willReturn(false);
		assertDoesNotThrow(() -> fileDataAccessService.duplicateBy(OriginalFileName, userId));
	}

	@Test
	@DisplayName("중복 파일 검사 : 실패")
	void shouldThrowExceptionDuplicateFile() {
		given(fileRepository.checkDuplicateFileName(OriginalFileName, userId)).willReturn(true);
		assertThrows(ServiceException.class, () -> fileDataAccessService.duplicateBy(OriginalFileName, userId));
	}
}
