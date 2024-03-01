package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record GetFolderReq(
	@Positive(message = "folder id should be positive") long folderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
