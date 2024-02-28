package com.c4cometrue.mystorage.rootfolder;

import com.c4cometrue.mystorage.TestConstants;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static com.c4cometrue.mystorage.TestConstants.*;

@DisplayName("루트폴더메타데이터 테스트")
class RootFolderMetadataTest {
    @Test
    @DisplayName("루트폴더메타데이터 빌더 테스트")
    void testRootFolderMetadataBuilder() {
        BigDecimal availableSpace = DataSizeConverter.gigabytesToBytes(2);
        BigDecimal usedSpace = BigDecimal.ZERO;

        RootFolderMetadata metadata = RootFolderMetadata.builder()
            .originalFolderName(USER_FOLDER_NAME)
            .storedFolderName(STORED_FOLDER_NAME)
            .ownerId(USER_ID)
            .build();

        Assertions.assertEquals(USER_FOLDER_NAME, metadata.getOriginalFolderName());
        Assertions.assertEquals(STORED_FOLDER_NAME, metadata.getStoredFolderName());
        Assertions.assertEquals(USER_ID, metadata.getOwnerId());
        Assertions.assertEquals(availableSpace, metadata.getAvailableSpace());
        Assertions.assertEquals(usedSpace, metadata.getUsedSpace());
    }
}
