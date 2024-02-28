package com.c4cometrue.mystorage.rootfolder;

import com.c4cometrue.mystorage.common.MetadataBaseEntity;
import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(name = "index_id_ownerId", columnList = "id, ownerId"),
    @Index(name = "index_storedFolderName", columnList = "storedFolderName"),
    @Index(name = "index_ownerId_originalFolderName", columnList = "ownerId, originalFolderName")
})
public class RootFolderMetadata extends MetadataBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String originalFolderName;
    @Column(nullable = false)
    private String storedFolderName;
    @Column(nullable = false)
    private Long ownerId;
    @PositiveOrZero
    private BigDecimal availableSpace;
    @PositiveOrZero
    private BigDecimal usedSpace;

    @Builder
    public RootFolderMetadata(String originalFolderName, String storedFolderName, Long ownerId) {
        this.originalFolderName = originalFolderName;
        this.storedFolderName = storedFolderName;
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

    public void increaseUsedSpace(BigDecimal fileSize) {
        if (usedSpace.add(fileSize).compareTo(availableSpace) > 0) {
            throw ErrorCode.EXCEEDED_CAPACITY.serviceException();
        }
        this.usedSpace = usedSpace.add(fileSize);
    }

    public void decreaseUsedSpace(BigDecimal fileSize) {
        if (fileSize.compareTo(usedSpace) > 0) {
            throw ErrorCode.INVALID_OPERATION.serviceException();
        }
        this.usedSpace = usedSpace.subtract(fileSize);
    }

    public BigDecimal calRemainSpace() {
        return this.availableSpace.subtract(this.usedSpace);
    }
}
