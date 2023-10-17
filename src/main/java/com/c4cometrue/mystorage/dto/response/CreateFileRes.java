package com.c4cometrue.mystorage.dto.response;

import com.c4cometrue.mystorage.entity.FileMetaData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateFileRes {
    private String fileStorageName;
    private long size;
    private String mime;
    private String owner;

    public CreateFileRes(FileMetaData fileMetaData) {
        this.fileStorageName = fileMetaData.getFileStorageName();
        this.size = fileMetaData.getSize();
        this.mime = fileMetaData.getMime();
        this.owner = fileMetaData.getOwner();
    }
}
