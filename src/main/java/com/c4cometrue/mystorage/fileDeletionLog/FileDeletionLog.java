package com.c4cometrue.mystorage.fileDeletionLog;

import jakarta.persistence.*;
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

    @Builder
    public FileDeletionLog(String originalFileName, String filePath, Long deleterId) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.deleterId = deleterId;
    }
}
