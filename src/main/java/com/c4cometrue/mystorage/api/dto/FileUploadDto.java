package com.c4cometrue.mystorage.api.dto;

import com.c4cometrue.mystorage.domain.FileMetaData;

public class FileUploadDto {

    public record Response(
        long fileId,
        long userId,
        String uploadFileName,
        long fileSize
    ) {
        public Response(FileMetaData fileMetaData) {
            this(fileMetaData.getId(), fileMetaData.getUserId(),
                fileMetaData.getUploadName(), fileMetaData.getSize());
        }
    }

}
