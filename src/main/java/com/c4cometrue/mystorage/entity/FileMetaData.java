package com.c4cometrue.mystorage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
public class FileMetaData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    @NotBlank(message = "file name is blank")
    private String fileName;

    @NotBlank(message = "file storage name is blank")
    private String fileStorageName;

    @NotNull(message = "file size can't be null")
    private long size;

    @NotBlank(message = "file content type is blank")
    private String mime;

    @NotBlank(message = "user name is blank")
    private String username;

    @Builder
    public FileMetaData(String fileName, String fileStorageName, long size, String mime, String username) {
        this.fileName = fileName;
        this.fileStorageName = fileStorageName;
        this.size = size;
        this.mime = mime;
        this.username = username;
    }

}
