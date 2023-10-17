package com.c4cometrue.mystorage.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;          //TODO: UUID

    private Long userId;

    private String fileName;

    private String uploadPath;

    private int size;

    private String type;

    @Builder
    public File(Long userId, String fileName, String uploadPath, int size, String type) {
        this.userId = userId;
        this.fileName = fileName;
        this.uploadPath = uploadPath;
        this.size = size;
        this.type = type;
    }
}
