package com.c4cometrue.mystorage.dto.response;

import org.springframework.core.io.Resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for {@link FileMetaDataRes}
 */
public record FileDownloadRes(
	@NotNull(message = "file is null") Resource resource,
	@NotBlank(message = "file name is blank") String fileName,
	@NotBlank(message = "file content type is blank") String mime) {
}
