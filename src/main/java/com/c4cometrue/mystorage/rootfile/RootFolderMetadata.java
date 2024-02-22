package com.c4cometrue.mystorage.rootfile;

import com.c4cometrue.mystorage.common.MetadataBaseEntity;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
public class RootFolderMetadata extends MetadataBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalFolderName;
    @Column(nullable = false)
    private String storedFolderName;
    @Column(nullable = false)
    private String filePath;
    @Column(nullable = false)
    private Long ownerId;
    private BigDecimal availableSpace;
    private BigDecimal usedSpace;

    @Builder
    public RootFolderMetadata(String originalFolderName, String storedFolderName, String filePath, Long ownerId) {
        this.originalFolderName = originalFolderName;
        this.storedFolderName = storedFolderName;
        this.filePath = filePath;
        this.ownerId = ownerId;
        this.availableSpace = DataSizeConverter.gigabytesToBytes(2);
        this.usedSpace = BigDecimal.ZERO;
    }

    public static String storedName() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedNow = now.format(formatter);
        return UUID.randomUUID() + formattedNow;
    }
}
