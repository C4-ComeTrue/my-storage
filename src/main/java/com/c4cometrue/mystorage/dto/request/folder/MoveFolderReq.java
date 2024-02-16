package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record MoveFolderReq(
	@NotNull(message = "folder id can't be null") long folderId,
	@NotNull(message = "target folder can't be null") long targetFolderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
