package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record CreateFolderReq(
	@Positive(message = "parent folder should be positive") long parentFolderId,
	@NotBlank(message = "user name is blank") String userName,
	@NotBlank(message = "folder name is blank") String folderName
) {
}
