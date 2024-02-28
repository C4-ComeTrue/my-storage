package com.c4cometrue.mystorage.folder;

import static com.c4cometrue.mystorage.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("폴더 엔티티 테스트")
class FolderMetadataTest {
    @Test
    @DisplayName("폴더 빌더 생성")
    void testBuilderCreation() {
        // When
        FolderMetadata folderMetadata = FolderMetadata.builder()
            .originalFolderName(USER_FOLDER_NAME)
            .storedFolderName(STORED_ROOT_FOLDER_NAME)
            .parentId(PARENT_ID)
            .filePath(FOLDER_PATH.toString())
            .uploaderId(USER_ID)
            .build();

        // Then
        assertEquals(USER_FOLDER_NAME, folderMetadata.getOriginalFolderName());
        assertEquals(STORED_ROOT_FOLDER_NAME, folderMetadata.getStoredFolderName());
        assertEquals(PARENT_ID, folderMetadata.getParentId());
        assertEquals(FOLDER_PATH.toString(), folderMetadata.getFilePath());
        assertEquals(USER_ID, folderMetadata.getUploaderId());
    }
}
