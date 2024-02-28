package com.c4cometrue.mystorage.storage;

import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.file.dto.FileContent;
import com.c4cometrue.mystorage.filedeletionlog.FileDeletionLogService;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.folder.dto.FolderContent;
import com.c4cometrue.mystorage.rootfolder.RootFolderService;
import com.c4cometrue.mystorage.util.PagingUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("스토리지 파사드 서비스 테스트")
class StorageFacadeServiceTest {
    @InjectMocks
    private StorageFacadeService storageFacadeService;
    @Mock
    private FolderService folderService;
    @Mock
    private FileService fileService;
    @Mock
    private FileDeletionLogService fileDeletionLogService;
    @Mock
    private RootFolderService rootFolderService;

    @Test
    @DisplayName("폴더조회테스트 flag가 true 일때")
    void getFolderContentsTrueTest() {
        CursorFolderResponse mockFolderResponse = CursorFolderResponse.of(
            List.of(FolderContent.of(FOLDER_ID, USER_FOLDER_NAME)), Boolean.FALSE);

        given(folderService.getFolders(PARENT_ID, FOLDER_ID, USER_ID, PagingUtil.createPageable(10)))
            .willReturn(mockFolderResponse);

        storageFacadeService.getFolderContents(PARENT_ID, FOLDER_ID, USER_ID, 10, true);

        verify(folderService, times(1)).getFolders(PARENT_ID, FOLDER_ID, USER_ID, PagingUtil.createPageable(10));
        verify(fileService, times(1)).getFiles(PARENT_ID, null, USER_ID, PagingUtil.createPageable(9));
    }

    @Test
    @DisplayName("폴더조회테스트 flag가 false 일 때")
    void getFolderContentsFalseTest() {
        CursorFileResponse mockFileResponse = CursorFileResponse.of(
            List.of(FileContent.of(FILE_ID, ORIGINAL_FILE_NAME)), Boolean.FALSE
        );

        given(fileService.getFiles(PARENT_ID, FILE_ID, USER_ID, PagingUtil.createPageable(10)))
            .willReturn(mockFileResponse);
        storageFacadeService.getFolderContents(PARENT_ID, FOLDER_ID, USER_ID, 10, false);

        verify(fileService, times(1)).getFiles(PARENT_ID, FILE_ID, USER_ID, PagingUtil.createPageable(10));
    }

    @Test
    @DisplayName("폴더 삭제 테스트")
    void deleteFolderContentsTest() {
        doNothing().when(folderService).validateBy(FOLDER_ID, USER_ID);
        when(folderService.findBy(FOLDER_ID, USER_ID)).thenReturn(FOLDER_METADATA);

        storageFacadeService.deleteFolderContents(FOLDER_ID, USER_ID);

        verify(folderService, times(1)).validateBy(FOLDER_ID, USER_ID);
        verify(folderService, times(1)).findBy(FOLDER_ID, USER_ID);
        verify(fileDeletionLogService, times(1)).saveFileDeleteLog(anyList());
    }
}
