package com.c4cometrue.mystorage.file;

import java.math.BigDecimal;
import java.util.UUID;

import com.c4cometrue.mystorage.common.MetadataBaseEntity;
import com.c4cometrue.mystorage.common.MetadataType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "file_metadata", indexes = @Index(name = "index_parentId", columnList = "parentId"))
public class FileMetadata extends MetadataBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false)
    private String storedFileName;
    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private Long uploaderId;
    private Long parentId;
    private Long rootId;
    private BigDecimal sizeInBytes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MetadataType metadataType;

    public static String storedName() {
        return UUID.randomUUID().toString();
    }

    public void changeParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Builder
    public FileMetadata(String originalFileName, String storedFileName, String filePath, Long uploaderId,
                        Long parentId, Long rootId, BigDecimal sizeInBytes) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.uploaderId = uploaderId;
        this.parentId = parentId;
        this.metadataType = MetadataType.FILE;
        this.rootId = rootId;
        this.sizeInBytes = sizeInBytes;
    }
}