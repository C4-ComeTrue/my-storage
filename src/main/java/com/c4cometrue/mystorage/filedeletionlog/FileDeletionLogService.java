package com.c4cometrue.mystorage.filedeletionlog;

import java.util.List;
import java.util.stream.Collectors;

import com.c4cometrue.mystorage.file.FileMetadata;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileDeletionLogService {
    private final FileDeletionLogRepository fileDeletionLogRepository;

    public void saveFileDeleteLog(List<FileMetadata> files) {
        List<FileDeletionLog> logs = files.stream()
                .map(file -> FileDeletionLog.builder()
                        .originalFileName(file.getOriginalFileName())
                        .filePath(file.getFilePath())
                        .deleterId(file.getUploaderId())
                        .sizeInBytes(file.getSizeInBytes())
                        .build())
                .collect(Collectors.toList());

        fileDeletionLogRepository.saveAll(logs);
    }
}
