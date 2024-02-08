package com.c4cometrue.mystorage.fileDeleteLog;

import com.c4cometrue.mystorage.filedeletionlog.FileDeletionLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.c4cometrue.mystorage.TestConstants.*;

@DisplayName("파일삭제로그 엔티티 테스트")
class FileDeleteLogTest {
    @Test
    @DisplayName("파일삭제로그 생성")
    void logCreationTest() {
        FileDeletionLog log = FileDeletionLog
                .builder()
                .originalFileName(FILE_METADATA.getOriginalFileName())
                .filePath(FILE_METADATA.getFilePath())
                .deleterId(FILE_METADATA.getUploaderId())
                .build();

        log.prePersist();

        Assertions.assertNotNull(log);
        Assertions.assertNotNull(log.getDeleteAt());
        Assertions.assertEquals(ORIGINAL_FILE_NAME, log.getOriginalFileName());
        Assertions.assertEquals(USER_PATH, log.getFilePath());
        Assertions.assertEquals(USER_ID, log.getDeleterId());
    }
}
