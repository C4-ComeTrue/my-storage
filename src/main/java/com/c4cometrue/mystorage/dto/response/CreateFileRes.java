package com.c4cometrue.mystorage.dto.response;

import com.c4cometrue.mystorage.entity.FileMetaData;

public record CreateFileRes(
    String fileStorageName,
    long size,
    String mime,
    String username

) {
    public CreateFileRes(FileMetaData fileMetaData) {
        this(
            fileMetaData.getFileStorageName(),
            fileMetaData.getSize(),
            fileMetaData.getMime(),
            fileMetaData.getUsername());
    }
}
