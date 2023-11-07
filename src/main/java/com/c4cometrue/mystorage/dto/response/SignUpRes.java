package com.c4cometrue.mystorage.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRes(
	@NotBlank(message = "user name is blank") String userName,
	@NotNull(message = "folder Id is null") long folderId
) {
}
