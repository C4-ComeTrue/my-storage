package com.c4cometrue.mystorage.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UploadFileReq(
	@NotNull(message = "file doesn't exist") MultipartFile file,
	@NotBlank(message = "user name is blank") String userName,
	@NotNull(message = "folder id is null") long folderId
) {
}
