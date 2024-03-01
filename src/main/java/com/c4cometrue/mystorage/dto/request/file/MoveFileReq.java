package com.c4cometrue.mystorage.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * DTO for {@link com.c4cometrue.mystorage.entity.FileMetaData}
 */
public record MoveFileReq(
	@Positive(message = "file id should be positive") long fileId,
	@Positive(message = "folder id should be positive") long folderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
