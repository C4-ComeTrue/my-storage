package com.c4cometrue.mystorage.folder.dto;

import com.c4cometrue.mystorage.file.dto.FileContentsRes;

public record FolderContentsRes(Long folderId, String fileName) {
	public static FolderContentsRes of (Long folderId, String fileName) {
		return new FolderContentsRes(folderId, fileName);
	}
}
