package com.c4cometrue.mystorage.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record FolderMetaDataRes(
	@NotNull(message = "folder pk is null") long folderId,
	@NotBlank(message = "folder name is blank") String folderName,
	@NotBlank(message = "user name is blank") String userName
) {
}
