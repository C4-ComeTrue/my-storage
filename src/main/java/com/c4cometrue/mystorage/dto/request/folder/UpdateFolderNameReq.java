package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record UpdateFolderNameReq(
	@Positive(message = "folder id should be positive") long folderId,
	@Positive(message = "parent folder id should be positive") long parentFolderId,
	@NotBlank(message = "user name is blank") String userName,
	@NotBlank(message = "new folder name is blank") String newFolderName
) {
}
