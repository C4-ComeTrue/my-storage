package com.c4cometrue.mystorage.dto;

import jakarta.validation.constraints.NotNull;

public record FolderContentsRequest(
	Long folderId,
	@NotNull(message = "사용자 id는 널이 될 수 없습니다") long userId
) {
	public static FolderContentsRequest of(Long folderId, long userId) {
		return new FolderContentsRequest(folderId, userId);
	}
}
