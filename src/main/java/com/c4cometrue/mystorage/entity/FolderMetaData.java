package com.c4cometrue.mystorage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
@Table(indexes = {
	@Index(name = "idx_folderName_parentId_userName", columnList = "folderName,parentFolderId,userName")
})
public class FolderMetaData {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long folderId;

	@NotBlank(message = "folder name is blank")
	private String folderName;

	@NotBlank(message = "user name is blank")
	private String userName;

	@NotNull(message = "parent folder can't be null")
	private Long parentFolderId;

	@Builder
	public FolderMetaData(String folderName, String userName, long parentFolderId) {
		this.folderName = folderName;
		this.userName = userName;
		this.parentFolderId = parentFolderId;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
}
