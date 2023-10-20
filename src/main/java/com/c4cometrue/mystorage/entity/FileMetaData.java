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

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileStorageName;

    @NotNull
    private long size;

    @NotBlank
    private String mime;

    @NotBlank
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
