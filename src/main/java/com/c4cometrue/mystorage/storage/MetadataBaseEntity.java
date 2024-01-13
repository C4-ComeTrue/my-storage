package com.c4cometrue.mystorage.storage;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public class MetadataBaseEntity {
	@Column(updatable = false)
	private LocalDate createdAt;
	private LocalDate updatedAt;

	@PrePersist
	public void prePersist() {
		LocalDate now = LocalDate.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDate.now();
	}
}
