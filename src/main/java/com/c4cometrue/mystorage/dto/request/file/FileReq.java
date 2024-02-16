package com.c4cometrue.mystorage.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FileMetaData
 */
public record FileReq(
	@NotNull(message = "file id is blank") long fileId,
	@NotBlank(message = "user name is blank") String userName,
	@NotNull(message = "folder id is blank") long folderId
) {
}
