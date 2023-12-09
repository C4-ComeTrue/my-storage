package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("폴더 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
	@InjectMocks
	private FolderService folderService;

	@Mock
	private FolderDataHandlerService folderDataHandlerService;

	@Test
	@DisplayName("폴더 업로드 테스트")
	void createBy() {
		// given
		given(folderDataHandlerService.findPathBy(PARENT_ID)).willReturn(PARENT_PATH);

		// when
		folderService.createBy(USER_ID, USER_FOLDER_NAME, PARENT_ID);

		//then
		verify(folderDataHandlerService, times(1)).findPathBy(PARENT_ID);
		verify(folderDataHandlerService, times(1)).persist(any(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트")
	void changeFolderNameTest() {
		folderService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);

		verify(folderDataHandlerService, times(1)).changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);
	}

	@Test
	@DisplayName("폴더 조회 테스트")
	void getFolder() {
		given(folderDataHandlerService.findChildBy(PARENT_ID, USER_ID)).willReturn(List.of(FOLDER_METADATA));

		folderService.findChildBy(PARENT_ID, USER_ID);

		then(folderDataHandlerService).should(times(1)).findChildBy(PARENT_ID, USER_ID);
	}
}
