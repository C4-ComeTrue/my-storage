package com.c4cometrue.mystorage.folder;

import java.util.UUID;

import com.c4cometrue.mystorage.meta.MetadataType;

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
@Table(name = "folder_metadata", indexes = @Index(name = "index_parentId", columnList = "parentId"))
public class FolderMetadata {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String originalFolderName;
	@Column(nullable = false)
	private String storedFolderName;

	private Long parentId;
	@Column(nullable = false)
	private String filePath;
	@Column(nullable = false)
	private Long uploaderId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MetadataType metadataType;

	public static String storedName(String userFolderName) {
		return userFolderName + UUID.randomUUID();
	}

	public void changeFolderName(String userFolderName) {
		this.originalFolderName = userFolderName;
	}

	@Builder
	public FolderMetadata(String originalFolderName, String storedFolderName, Long parentId, String filePath,
		Long uploaderId) {
		this.originalFolderName = originalFolderName;
		this.storedFolderName = storedFolderName;
		this.parentId = parentId;
		this.filePath = filePath;
		this.uploaderId = uploaderId;
		this.metadataType = MetadataType.FOLDER;
	}
}