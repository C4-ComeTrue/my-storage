package com.c4cometrue.mystorage.repository;

import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.domain.FileMetaData;

@ExtendWith(MockitoExtension.class)
class FileMetaDataWriterTest {

	@Mock
	FileMetaDataRepository repository;

	@InjectMocks
	FileMetaDataWriter fileMetaDataWriter;

	@Test
	void 파일_메타데이터를_저장한다() {
		// given
		var multipartFile = mock(MultipartFile.class);
		var userId = 1L;
		var fileName = "name";
		var parent = mock(FileMetaData.class);

		// when
		var response = fileMetaDataWriter.saveFileMetaData(multipartFile, userId, fileName, parent);

		// then
		verify(repository, times(1)).save(any());
	}

	@Test
	void 폴더_메타데이터를_저장한다() {
		// given
		var userId = 1L;
		var fileName = "name";
		var parent = mock(FileMetaData.class);

		// when
		var response = fileMetaDataWriter.saveFolderMetaData(userId, fileName, parent);

		// then
		verify(repository, times(1)).save(any());
	}

	@Test
	void 파일_메타데이터를_삭제한다() {
		// given
		var fileMetaData = mock(FileMetaData.class);

		// when
		fileMetaDataWriter.delete(fileMetaData);

		// then
		verify(repository, times(1)).delete(any());
	}

}
