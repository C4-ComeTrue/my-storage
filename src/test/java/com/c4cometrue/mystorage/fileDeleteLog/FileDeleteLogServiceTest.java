package com.c4cometrue.mystorage.fileDeleteLog;

import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.fileDeletionLog.FileDeletionLog;
import com.c4cometrue.mystorage.fileDeletionLog.FileDeletionLogRepository;
import com.c4cometrue.mystorage.fileDeletionLog.FileDeletionLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.c4cometrue.mystorage.TestConstants.FILE_METADATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("파일삭제로그 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class FileDeleteLogServiceTest {
    @InjectMocks
    private FileDeletionLogService fileDeletionLogService;

    @Mock
    private FileDeletionLogRepository fileDeletionLogRepository;

    @Test
    void saveFileDeleteLog() {
        List<FileMetadata> files = List.of(FILE_METADATA);

        fileDeletionLogService.saveFileDeleteLog(files);

        verify(fileDeletionLogRepository, times(1)).saveAll(any());
    }

}
