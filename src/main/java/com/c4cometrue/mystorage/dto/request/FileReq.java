package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @see com.c4cometrue.mystorage.entity.FileMetaData
 */
public record FileReq(
	@NotBlank(message = "file storage name is blank") String fileStorageName,
	@NotBlank(message = "user name is blank") String userName,
	@NotNull(message = "folder id is blank") long folderId
) {
}
