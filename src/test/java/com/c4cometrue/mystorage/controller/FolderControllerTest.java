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
		var mockGetFolderReq = new GetFolderReq(1L, "my_folder", mockUserName, 0L);

		// when
		folderController.getFolderData(mockGetFolderReq);

		// then
		verify(folderService, times(1)).getFolderData(mockGetFolderReq);

	}

	@Test
	@DisplayName("폴더 생성")
	void createFolder() {
		// given
		var mockCreateFolderReq = new CreateFolderReq("my_folder", mockUserName, 0L);

		// when
		folderController.createFolder(mockCreateFolderReq);

		// then
		verify(folderService, times(1)).createFolder(mockCreateFolderReq);
	}

	@Test
	@DisplayName("폴더 이름 수정")
	void updateFolderName() {
		// given
		var mockUpdateFolderNameReq = new UpdateFolderNameReq("my_folder", mockUserName, "new_folder", 0L);

		// when
		folderController.updateFolderName(mockUpdateFolderNameReq);

		// then
		verify(folderService, times(1)).updateFolderName(mockUpdateFolderNameReq);
	}

}
