package com.c4cometrue.mystorage.metadata;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.dto.FileUploadRequest;
import com.c4cometrue.mystorage.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.meta.MetadataController;
import com.c4cometrue.mystorage.meta.MetadataService;

@ExtendWith(MockitoExtension.class)
@DisplayName("메타데이타 컨트롤러 테스트")
class MetadataControllerTest {
	@InjectMocks
	private MetadataController metadataController;

	@Mock
	private MetadataService metadataService;

	@Test
	@DisplayName("업로드 파일 테스트")
	void uploadFileTest() {
		metadataController.uploadFile(FileUploadRequest.of(MOCK_MULTIPART_FILE, USER_ID, PARENT_ID));
		verify(metadataService, times(1)).uploadFile(MOCK_MULTIPART_FILE, USER_ID, PARENT_ID);
	}

	@Test
	@DisplayName("특정 폴더 상세 조회 테스트")
	void getFolderContentsTest() {
		given(metadataService.getFolderContents(FOLDER_ID, USER_ID)).willReturn(eq(anyList()));
		metadataController.getFolderContents(FolderContentsRequest.of(FOLDER_ID, USER_ID));
		verify(metadataService, times(1)).getFolderContents(FOLDER_ID, USER_ID);
	}
}
