package com.c4cometrue.mystorage.file.entity;

import com.c4cometrue.mystorage.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class FileMetaData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_mine", nullable = false)
    private String fileMine;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name  = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "saved_path", nullable = false)
    private String savedPath;

    @Builder
    public FileMetaData(String fileName, String fileMine, String userName, Long fileSize, String savedPath) {
        this.fileName = fileName;
        this.userName = userName;
        this.fileMine = fileMine;
        this.fileSize = fileSize;
        this.savedPath = savedPath;
    }
}
