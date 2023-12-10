package com.c4cometrue.mystorage.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record CreateFolderRes(
	@NotNull(message = "folder Number can't be null") long folderId,
	@NotBlank(message = "folder name is blank") String folderName,
	@NotBlank(message = "user name is blank") String userName
) {
}
