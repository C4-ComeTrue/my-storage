package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.c4cometrue.mystorage.rootfolder.RootFolderService;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.util.PagingUtil;

@DisplayName("폴더 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FolderServiceTest {
    @InjectMocks
    private FolderService folderService;

    @Mock
    private FolderDataHandlerService folderDataHandlerService;

    @Mock
    private RootFolderService rootFolderService;

    @Test
    @DisplayName("폴더 업로드 테스트")
    void createBy() {
        // given
        doNothing().when(rootFolderService).checkValidateBy(ROOT_ID, USER_ID);
        given(folderDataHandlerService.findPathBy(PARENT_ID, USER_ID)).willReturn(PARENT_PATH);

        // when
        folderService.createBy(USER_ID, USER_FOLDER_NAME, PARENT_ID, ROOT_ID);

        //then
        verify(folderDataHandlerService, times(1)).findPathBy(PARENT_ID, USER_ID);
        verify(folderDataHandlerService, times(1)).persist(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("폴더 이름 변경 테스트")
    void changeFolderNameTest() {
        folderService.changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);

        verify(folderDataHandlerService, times(1)).changeFolderNameBy(USER_FOLDER_NAME, FOLDER_ID, USER_ID);
    }

    @Test
    @DisplayName("폴더 조회 테스트")
    void getFoldersTest() {

        given(folderDataHandlerService.getFolderList(PARENT_ID, FOLDER_ID, USER_ID, PagingUtil.createPageable(10)))
            .willReturn(List.of(FOLDER_METADATA));
        given(folderDataHandlerService.hasNext(PARENT_ID, USER_ID, FOLDER_METADATA.getId()))
            .willReturn(Boolean.FALSE);

        CursorFolderResponse response = folderService.getFolders(PARENT_ID, FOLDER_ID, USER_ID,
            PagingUtil.createPageable(10));

        assertNotNull(response);
        then(folderDataHandlerService).should(times(1))
            .getFolderList(PARENT_ID, FOLDER_ID, USER_ID, PagingUtil.createPageable(10));
        then(folderDataHandlerService).should(times(1)).hasNext(PARENT_ID, USER_ID, FOLDER_METADATA.getId());
    }

    @Test
    @DisplayName("유효성 검사")
    void validateTest() {
        folderService.validateBy(FOLDER_ID, USER_ID);

        verify(folderDataHandlerService, times(1)).validateFolderOwnershipBy(FOLDER_ID, USER_ID);
    }

    @Test
    @DisplayName("폴더 이동 테스트")
    void moveFolderTest() {
        Long destinationFolderID = 2L;
        doNothing().when(folderDataHandlerService).validateFolderOwnershipBy(destinationFolderID, USER_ID);
        when(folderDataHandlerService.findBy(FOLDER_ID, USER_ID)).thenReturn(FOLDER_METADATA);


        folderService.moveFolder(FOLDER_ID, USER_ID, destinationFolderID);

        verify(folderDataHandlerService).validateFolderOwnershipBy(destinationFolderID, USER_ID);

        verify(folderDataHandlerService, times(1)).findBy(FOLDER_ID, USER_ID);
    }

    @Test
    @DisplayName("폴더 리스트 조회")
    void findAllFolderListTest() {
        given(folderDataHandlerService.findAllBy(PARENT_ID)).willReturn(List.of(FOLDER_METADATA));
        folderService.findAllBy(PARENT_ID);
        then(folderDataHandlerService).should(times(1)).findAllBy(PARENT_ID);
    }

    @Test
    @DisplayName("폴더 조회")
    void findFolderTest() {
        given(folderDataHandlerService.findBy(FOLDER_ID, USER_ID)).willReturn(FOLDER_METADATA);
        folderService.findBy(FOLDER_ID, USER_ID);
        then(folderDataHandlerService).should(times(1)).findBy(FOLDER_ID, USER_ID);
    }

    @Test
    @DisplayName("폴더 경로 조회 테스트")
    void findPathTest() {
        given(folderDataHandlerService.findPathBy()).willReturn(PARENT_PATH);

        String actualPath = folderService.findPathBy();

        assertThat(actualPath).isEqualTo(PARENT_PATH);
        verify(folderDataHandlerService, times(1)).findPathBy();
    }

    @Test
    @DisplayName("폴더 삭제 테스트")
    void deleteFolderTest() {
        doNothing().when(folderDataHandlerService).delete(FOLDER_METADATA);

        folderService.deleteFolder(FOLDER_METADATA);

        verify(folderDataHandlerService, times(1)).delete(FOLDER_METADATA);
    }

    @Test
    @DisplayName("폴더 요약 테스트")
    void getFolderSummaryTest() {
        given(folderDataHandlerService.findBy(FOLDER_ID, USER_ID)).willReturn(FOLDER_METADATA);

        folderService.getFolderSummary(FOLDER_ID, USER_ID);

        verify(folderDataHandlerService, times(1)).findBy(FOLDER_ID, USER_ID);
    }
}
