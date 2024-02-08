package com.c4cometrue.mystorage.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;

@Getter
@MappedSuperclass
public class MetadataBaseEntity {
    @Column(updatable = false)
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = ZonedDateTime.now(ZoneOffset.UTC);
    }
}
