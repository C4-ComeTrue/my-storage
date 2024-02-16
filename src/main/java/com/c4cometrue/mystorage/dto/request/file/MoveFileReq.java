package com.c4cometrue.mystorage.dto.request.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link com.c4cometrue.mystorage.entity.FileMetaData}
 */
public record MoveFileReq(
	@NotNull(message = "file id is blank") long fileId,
	@NotNull(message = "folder id is blank") long folderId,
	@NotBlank(message = "user name is blank") String userName
) {
}
