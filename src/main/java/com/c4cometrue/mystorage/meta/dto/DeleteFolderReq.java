package com.c4cometrue.mystorage.meta.dto;

public record DeleteFolderReq(long folderId, long userId) {
	public static DeleteFolderReq of (long folderId, long userId) {
		return new DeleteFolderReq(folderId, userId);
	}
}
