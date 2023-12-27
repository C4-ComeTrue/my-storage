package com.c4cometrue.mystorage.metadata;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.folder.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.meta.MetadataController;
import com.c4cometrue.mystorage.meta.StorageFacadeService;

@DisplayName("메타데이터 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class MetadataControllerTest {
	@InjectMocks
	private MetadataController metadataController;

	@Mock
	private StorageFacadeService storageFacadeService;

	@Test
	@DisplayName("폴더 조회 컨트롤러 테스트")
	void getContents() {
		metadataController.getFolderContents(FolderContentsRequest.of(PARENT_ID, FOLDER_ID, USER_ID, null, true));
		verify(storageFacadeService, times(1)).getFolderContents(PARENT_ID, FOLDER_ID, USER_ID, null, true);
	}
}
