package com.c4cometrue.mystorage.dto;

import jakarta.validation.constraints.NotNull;

public record FolderCreateRequest(@NotNull long userId, @NotNull String userFolderName, Long parentId) {
	public static FolderCreateRequest of(long userId, String userFolderName, long parentId) {
		return new FolderCreateRequest(userId, userFolderName, parentId);
	}
}
