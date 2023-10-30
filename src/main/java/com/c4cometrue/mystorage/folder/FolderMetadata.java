package com.c4cometrue.mystorage.folder;

import java.util.UUID;

import jakarta.persistence.Entity;
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
@Table(name = "Folder_Metadata", indexes = @Index(name = "index_parentId", columnList = "parentId"))
public class FolderMetadata {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String originalFolderName;

	private String storedFolderName;

	private Long parentId;

	private String filePath;

	private Long userId;

	public static String storedName(String userFolderName) {
		return userFolderName + UUID.randomUUID();
	}

	public void changeFolderName(String userFolderName) {
		this.originalFolderName = userFolderName;
	}
	@Builder
	public FolderMetadata(String originalFolderName, String storedFolderName, Long parentId, String filePath,
		Long userId) {
		this.originalFolderName = originalFolderName;
		this.storedFolderName = storedFolderName;
		this.parentId = parentId;
		this.filePath = filePath;
		this.userId = userId;
	}
}
