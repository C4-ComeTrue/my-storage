package com.c4cometrue.mystorage.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * @see com.c4cometrue.mystorage.entity.FileMetaData
 */
public record FileReq(
	@Positive(message = "file id should be positive") long fileId,
	@NotBlank(message = "user name is blank") String userName,
	@Positive(message = "folder id should be positive") long folderId
) {
}
