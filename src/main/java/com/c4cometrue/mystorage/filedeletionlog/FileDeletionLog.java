package com.c4cometrue.mystorage.filedeletionlog;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDeletionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalFileName;
    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private Long deleterId;

    @Column(updatable = false)
    private ZonedDateTime deleteAt;

    @Builder
    public FileDeletionLog(String originalFileName, String filePath, Long deleterId) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.deleterId = deleterId;
    }

    @PrePersist
    public void prePersist() {
        deleteAt = ZonedDateTime.now(ZoneOffset.UTC);
    }
}
