package com.c4cometrue.mystorage.file.dto;

public record FileMoveReq(Long userId, Long fileId, Long destinationFolderId) {
	public static FileMoveReq of(long userId, long fileId, long destinationFolderId) {
		return new FileMoveReq(userId, fileId, destinationFolderId);
	}
}
