package com.c4cometrue.mystorage.dto.request.file;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UploadFileReq(
	@NotNull(message = "file doesn't exist") MultipartFile file,
	@NotBlank(message = "user name is blank") String userName,
	@Positive(message = "folder id should be positive") long folderId
) {
}
