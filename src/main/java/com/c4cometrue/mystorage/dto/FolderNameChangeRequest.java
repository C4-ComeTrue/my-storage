package com.c4cometrue.mystorage.dto;

public record FolderNameChangeRequest(
	Long userId,
	Long folderId,
	String folderName
) {
	public static FolderNameChangeRequest of(
		Long userId,
		Long folderId,
		String folderName) {
		return new FolderNameChangeRequest(userId, folderId, folderName);
	}
}
