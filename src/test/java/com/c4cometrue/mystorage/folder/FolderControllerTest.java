package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.*;

import com.c4cometrue.mystorage.folder.dto.FolderSummaryReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.folder.dto.FolderCreateRequest;
import com.c4cometrue.mystorage.folder.dto.FolderMoveReq;

@DisplayName("폴더 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class FolderControllerTest {
    @InjectMocks
    private FolderController folderController;

    @Mock
    private FolderService folderService;

    @Test
    @DisplayName("폴더 생성 테스트")
    void createFolder() {
        folderController.createFolder(FolderCreateRequest.of(USER_ID, USER_FOLDER_NAME, PARENT_ID, ROOT_ID));
        verify(folderService, times(1)).createBy(USER_ID, USER_FOLDER_NAME, PARENT_ID, ROOT_ID);
    }

    @Test
    @DisplayName("폴더 이름 변경 테스트")
    void changeFolderName() {
        folderController.changeFolderNameBy(FOLDER_NAME_CHANGE_REQUEST);

        verify(folderService, times(1)).changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);
    }

    @Test
    @DisplayName("폴더 이동 컨트롤러 테스트")
    void moveFolderTest() {
        folderController.moveFolder(FolderMoveReq.of(FOLDER_ID, USER_ID, 2L));

        verify(folderService, times(1)).moveFolder(FOLDER_ID, USER_ID, 2L);
    }

    @Test
    @DisplayName("폴더 요약 컨트롤러 테스트")
    void getFolderSummaryTest() {
        folderController.getFolderSummary(FolderSummaryReq.of(FOLDER_ID, USER_ID));

        verify(folderService, times(1)).getFolderSummary(FOLDER_ID, USER_ID);
    }
}
