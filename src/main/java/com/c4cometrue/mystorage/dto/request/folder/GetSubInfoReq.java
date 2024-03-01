package com.c4cometrue.mystorage.dto.request.folder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record GetSubInfoReq(
	@Positive(message = "folder id should be positive") long folderId,
	@Min(value = 0, message = "page number should be 0 or positive") int page
) {
}