package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;
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
	private FolderReader folderReader;

	@Mock
	private FolderWriter folderWriter;

	@Mock
	private FolderMetadata folderMetadata;

	@Test
	@DisplayName("폴더 업로드 테스트")
	void createBy() {
		// given
		given(folderReader.findPathBy(PARENT_ID)).willReturn(PARENT_PATH);

		// when
		folderService.createBy(USER_ID, USER_FOLDER_NAME, PARENT_ID);

		//then
		verify(folderReader, times(1)).findPathBy(PARENT_ID);
		verify(folderWriter, times(1)).persist(any(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트")
	void changeFolderNameTest() {
		folderService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);

		verify(folderWriter, times(1)).changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);
	}
}
