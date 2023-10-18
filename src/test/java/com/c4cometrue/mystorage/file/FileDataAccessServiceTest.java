package com.c4cometrue.mystorage.file;

import static com.c4cometrue.mystorage.file.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.c4cometrue.mystorage.exception.ServiceException;

@DisplayName("파일 데이터어세스 서비스 테스트")
class FileDataAccessServiceTest {
	@InjectMocks
	private FileDataAccessService fileDataAccessService;
	@Mock
	FileRepository fileRepository;

	@BeforeEach
	public void setUp(){
		MockitoAnnotations.openMocks(this);
	}


	@Test
	@DisplayName("파일 삭제 테스트")
	void shouldDeleteByFileId(){
		fileDataAccessService.deleteBy(fileId);
		verify(fileRepository, times(1)).deleteById(fileId);
	}

	@Test
	@DisplayName("파일 조회 테스트")
	void shouldFindByFileId(){
		Metadata metadata = TestConstants.METADATA;
		when(fileRepository.findById(fileId)).thenReturn(Optional.of(metadata));

		Metadata result = fileDataAccessService.findBy(fileId);

		verify(fileRepository, times(1)).findById(fileId);
	}

	@Test
	@DisplayName("파일 조회 테스트 : 실패")
	void shouldThrowExceptionWhenFileNotFount(){
		when(fileRepository.findById(fileId)).thenReturn(Optional.empty());
		assertThrows(ServiceException.class,
			() -> fileDataAccessService.findBy(fileId)
		);
	}

	@Test
	@DisplayName("파일 저장 테스트")
	void shouldPersistFile(){
		Metadata metadata = TestConstants.METADATA;

		fileDataAccessService.persist(metadata);

		verify(fileRepository, times(1)).save(TestConstants.METADATA);
	}
}
