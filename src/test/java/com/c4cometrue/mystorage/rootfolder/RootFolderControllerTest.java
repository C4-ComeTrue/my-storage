package com.c4cometrue.mystorage.rootfolder;

import com.c4cometrue.mystorage.TestConstants;
import com.c4cometrue.mystorage.rootfolder.dto.CreateRootFolderReq;
import com.c4cometrue.mystorage.rootfolder.dto.RootInfoReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("루트 폴더 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class RootFolderControllerTest {
    @InjectMocks
    private RootFolderController rootFolderController;

    @Mock
    private RootFolderService rootFolderService;

    @Test
    @DisplayName("루트 폴더 생성 테스트")
    void createRootFolderTest() {
        rootFolderController.createRootFolder(CreateRootFolderReq.of(USER_ID, USER_FOLDER_NAME));
        verify(rootFolderService, times(1)).createBy(USER_ID, USER_FOLDER_NAME);
    }

    @Test
    @DisplayName("루트 폴더 요약 테스트")
    void getRootInfoTest() {
        rootFolderController.getRootInfo(RootInfoReq.of(ROOT_ID, USER_ID));
        verify(rootFolderService, times(1)).getRootInfo(ROOT_ID, USER_ID);
    }
}
