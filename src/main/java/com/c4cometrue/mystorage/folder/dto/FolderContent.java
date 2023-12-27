package com.c4cometrue.mystorage.folder.dto;

public record FolderContent(Long folderId, String fileName) {
	public static FolderContent of(Long folderId, String fileName) {
		return new FolderContent(folderId, fileName);
	}
}
