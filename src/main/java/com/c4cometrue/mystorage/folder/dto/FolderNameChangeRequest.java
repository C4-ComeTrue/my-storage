package com.c4cometrue.mystorage.folder.dto;

import jakarta.validation.constraints.NotNull;

public record FolderNameChangeRequest(
		@NotNull(message = "유저id는 널이 될 수 없습니다") long userId,
		@NotNull(message = "폴더id는 널이 될 수 없습니다") long folderId,
	String folderName
) {
	public static FolderNameChangeRequest of(
		long userId,
		Long folderId,
		String folderName) {
		return new FolderNameChangeRequest(userId, folderId, folderName);
	}
}
