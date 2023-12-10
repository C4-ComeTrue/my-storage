package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FolderMetaData
 */
public record GetFolderReq(
	@NotNull long folderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
