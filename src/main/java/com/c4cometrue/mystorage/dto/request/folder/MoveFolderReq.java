package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record MoveFolderReq(
	@Positive(message = "folder id should be positive") long folderId,
	@Positive(message = "target folder should be positive") long targetFolderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
