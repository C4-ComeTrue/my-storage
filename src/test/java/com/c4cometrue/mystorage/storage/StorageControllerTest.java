package com.c4cometrue.mystorage.storage;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.folder.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.storage.dto.DeleteFolderReq;

@DisplayName("메타데이터 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class StorageControllerTest {
	@InjectMocks
	private StorageController storageController;

	@Mock
	private StorageFacadeService storageFacadeService;

	@Test
	@DisplayName("폴더 조회 컨트롤러 테스트")
	void getContents() {
		storageController.getFolderContents(FolderContentsRequest.of(PARENT_ID, FOLDER_ID, USER_ID, null, true));
		verify(storageFacadeService, times(1)).getFolderContents(PARENT_ID, FOLDER_ID, USER_ID, null, true);
	}

	@Test
	@DisplayName("폴더 삭제 컨트롤러 테스트")
	void deleteFolderTest() {
		storageController.deleteFolder(DeleteFolderReq.of(FOLDER_ID, USER_ID));

		verify(storageFacadeService, times(1)).deleteFolderContents(FOLDER_ID, USER_ID);
	}
}
