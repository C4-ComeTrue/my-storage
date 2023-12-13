package com.c4cometrue.mystorage.metadata;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.meta.MetadataController;
import com.c4cometrue.mystorage.meta.StorageFacadeService;

@ExtendWith(MockitoExtension.class)
@DisplayName("메타데이타 컨트롤러 테스트")
class MetadataControllerTest {
	@InjectMocks
	private MetadataController metadataController;

	@Mock
	private StorageFacadeService storageFacadeService;

	@Test
	@DisplayName("특정 폴더 상세 조회 테스트")
	void getFolderContentsTest() {
		given(storageFacadeService.getFolderContents(FOLDER_ID, USER_ID)).willReturn(eq(anyList()));
		metadataController.getFolderContents(FolderContentsRequest.of(FOLDER_ID, USER_ID));
		verify(storageFacadeService, times(1)).getFolderContents(FOLDER_ID, USER_ID);
	}
}
