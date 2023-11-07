package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link com.c4cometrue.mystorage.entity.FolderMetaData}
 */
public record CreateFolderReq(
	@NotBlank(message = "folder name is blank") String folderName,
	@NotBlank(message = "user name is blank") String userName,
	@NotNull(message = "parent folder can't be null") long parentFolderId) {
}
