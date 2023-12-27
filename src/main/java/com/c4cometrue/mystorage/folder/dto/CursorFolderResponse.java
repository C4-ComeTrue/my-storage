package com.c4cometrue.mystorage.folder.dto;

import java.util.List;

import com.c4cometrue.mystorage.folder.FolderMetadata;

public record CursorFolderResponse(List<FolderMetadata> folderMetadata, Boolean folderHasNext) {
	public static CursorFolderResponse of(List<FolderMetadata> folderMetadata, Boolean folderHasNext) {
		return new CursorFolderResponse(folderMetadata, folderHasNext);
	}
}
