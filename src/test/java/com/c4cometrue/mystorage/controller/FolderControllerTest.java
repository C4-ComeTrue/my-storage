package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.dto.request.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.UpdateFolderNameReq;
import com.c4cometrue.mystorage.service.FolderService;

@ExtendWith(MockitoExtension.class)
class FolderControllerTest {
	@InjectMocks
	private FolderController folderController;
	@Mock
	private FolderService folderService;

	@Test
	@DisplayName("폴더 조회")
	void getFolderData() {
		// given
		var req = new GetFolderReq(1L, MOCK_USER_NAME);

		// when
		folderController.getFolderData(req);

		// then
		verify(folderService, times(1)).getFolderData(req.folderId(), req.userName());

	}

	@Test
	@DisplayName("폴더 생성")
	void createFolder() {
		// given
		var req = new CreateFolderReq(0L, MOCK_USER_NAME, "my_folder");

		// when
		folderController.createFolder(req);

		// then
		verify(folderService, times(1)).createFolder(req.parentFolderId(), req.userName(), req.folderName());
	}

	@Test
	@DisplayName("폴더 이름 수정")
	void updateFolderName() {
		// given
		var req = new UpdateFolderNameReq(1L, 0L, MOCK_USER_NAME, "new_folder");

		// when
		folderController.updateFolderName(req);

		// then
		verify(folderService, times(1)).updateFolderName(req.folderId(), req.parentFolderId(), req.userName(),
			req.newFolderName());
	}

}
