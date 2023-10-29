package com.c4cometrue.mystorage.file.dto;

public record FileDownloadRequestDto(String fileName, String userName, String downloadPath) {
    public static FileDownloadRequestDto create(
            String fileName, String userName, String downloadPath
    ) {
        return new FileDownloadRequestDto(fileName, userName, downloadPath);
    }
}
