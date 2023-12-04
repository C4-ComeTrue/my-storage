package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record CreateFolderReq(
	@NotNull(message = "parent folder can't be null") long parentFolderId,
	@NotBlank(message = "user name is blank") String userName,
	@NotBlank(message = "folder name is blank") String folderName
) {
}
