package com.c4cometrue.mystorage.folder.dto;

public record FolderMoveReq(long folderId, long userId, long destinationFolderId) {
	public static FolderMoveReq of (long folderId, long userId, long destinationFolderId) {
		return new FolderMoveReq(folderId, userId, destinationFolderId);
	}
}
