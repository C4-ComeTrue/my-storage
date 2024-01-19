package com.c4cometrue.mystorage.deletedmetadata;

import java.time.LocalDate;

import com.c4cometrue.mystorage.common.MetadataType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletedMetadata {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private Long parentId;
	private String filePath;
	private MetadataType type;
	private LocalDate deletedDate;

	@PreUpdate
	public void prePersist() {
		deletedDate = LocalDate.now();
	}

	@Builder
	public DeletedMetadata(Long userId, Long parentId, String filePath, MetadataType type) {
		this.userId = userId;
		this.parentId = parentId;
		this.filePath = filePath;
		this.type = type;
	}
}
