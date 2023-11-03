package com.c4cometrue.mystorage.metadata;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.meta.MetadataService;

@ExtendWith(MockitoExtension.class)
@DisplayName("메타데이터 서비스 테스트")
class MetadataServiceTest {
	@InjectMocks
	private MetadataService metadataService;

	@Mock
	private FolderService folderService;

	@Mock
	private FileService fileService;

	@Test
	@DisplayName("업로드 파일 테스트")
	void uploadFile() {
		given(folderService.findPathBy(PARENT_ID)).willReturn(PARENT_PATH);
		doNothing().when(fileService).uploadFile(MOCK_MULTIPART_FILE, FILE_ID, PARENT_ID, PARENT_PATH);

		metadataService.uploadFile(MOCK_MULTIPART_FILE, FILE_ID, PARENT_ID);

		verify(folderService, times(1)).findPathBy(PARENT_ID);
		verify(fileService, times(1)).uploadFile(MOCK_MULTIPART_FILE, FILE_ID, PARENT_ID, PARENT_PATH);
	}

	@Test
	@DisplayName("폴더 조회 테스트")
	void getFolderContentsTest() {
		given(folderService.findChildBy(PARENT_ID, USER_ID)).willReturn(List.of(FOLDER_METADATA));
		given(fileService.findChildBy(PARENT_ID, USER_ID)).willReturn(List.of(FILE_METADATA));

		metadataService.getFolderContents(PARENT_ID, USER_ID);

		verify(folderService, times(1)).findChildBy(PARENT_ID, USER_ID);
		verify(fileService, times(1)).findChildBy(PARENT_ID, USER_ID);
	}
}
