package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.dto.request.folder.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.DeleteFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.GetSubInfoReq;
import com.c4cometrue.mystorage.dto.request.folder.MoveFolderReq;
import com.c4cometrue.mystorage.dto.request.folder.UpdateFolderNameReq;
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
		verify(folderService, times(1)).getFolderTotalInfo(req.folderId(), req.userName());
	}

	@Test
	@DisplayName("폴더의 하위 폴더 n 페이지 조회")
	void getFolderSubFolders() {
		// given
		var folderId = 1L;
		var pageNumber = 1;
		var req = new GetSubInfoReq(folderId, pageNumber);

		// when
		folderController.getSubFolders(req);

		// then
		verify(folderService, times(1)).getFolders(folderId, pageNumber);

	}


	@Test
	@DisplayName("폴더의 하위 파일 n 페이지 조회")
	void getFolderSubFiles() {
		// given
		var folderId = 1L;
		var pageNumber = 1;
		var req = new GetSubInfoReq(folderId, pageNumber);

		// when
		folderController.getSubFiles(req);

		// then
		verify(folderService, times(1)).getFiles(folderId, pageNumber);

	}

	@Test
	@DisplayName("폴더 생성")
	void createFolder() {
		// given
		var req = new CreateFolderReq(1L, MOCK_USER_NAME, "my_folder");

		// when
		folderController.createFolder(req);

		// then
		verify(folderService, times(1)).createFolder(req.parentFolderId(), req.userName(), req.folderName());
	}

	@Test
	@DisplayName("폴더 이름 수정")
	void updateFolderName() {
		// given
		var req = new UpdateFolderNameReq(2L, 1L, MOCK_USER_NAME, "new_folder");

		// when
		folderController.updateFolderName(req);

		// then
		verify(folderService, times(1)).updateFolderName(req.folderId(), req.parentFolderId(), req.userName(),
			req.newFolderName());
	}

	@Test
	@DisplayName("폴더 이동")
	void moveFolder() {
		// given
		var req = new MoveFolderReq(1L, 99L, MOCK_USER_NAME);

		// when
		folderController.moveFolder(req);

		// then
		verify(folderService, times(1)).moveFolder(req.folderId(), req.targetFolderId(), req.userName());
	}

	@Test
	@DisplayName("폴더 삭제")
	void deleteFolder() {
		// given
		var req = new DeleteFolderReq(1L, MOCK_USER_NAME);

		// when
		folderController.deleteFolder(req);

		// then
		verify(folderService, times(1)).deleteFolder(req.folderId(), req.userName());
	}

}
