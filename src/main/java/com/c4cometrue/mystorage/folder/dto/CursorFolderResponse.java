package com.c4cometrue.mystorage.folder.dto;

import java.util.List;

public record CursorFolderResponse(List<FolderContent> folderMetadata, Boolean folderHasNext) {
	public static CursorFolderResponse of(List<FolderContent> folderContents, Boolean folderHasNext) {
		return new CursorFolderResponse(folderContents, folderHasNext);
	}
}
