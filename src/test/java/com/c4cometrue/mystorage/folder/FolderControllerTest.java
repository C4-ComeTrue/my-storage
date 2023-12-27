package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.folder.dto.FolderCreateRequest;

@DisplayName("폴더 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class FolderControllerTest {
	@InjectMocks
	private FolderController folderController;

	@Mock
	private FolderService folderService;

	@Test
	@DisplayName("폴더 생성 테스트")
	void creatFolder(){
		folderController.createFolder(FolderCreateRequest.of(USER_ID, USER_FOLDER_NAME, PARENT_ID));
		verify(folderService, times(1)).createBy(USER_ID, USER_FOLDER_NAME, PARENT_ID);
	}

	@Test
	@DisplayName("폴더 이름 변경 테스트")
	void changeFolderName(){
		folderController.changeFolderNameBy(FOLDER_NAME_CHANGE_REQUEST);

		verify(folderService, times(1)).changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);
	}
}
