package com.c4cometrue.mystorage.fileDeletionLog;

public record FileDeletionInfo(
        String originalFileName,
        String filePath,
        Long deleterId
) {
}
