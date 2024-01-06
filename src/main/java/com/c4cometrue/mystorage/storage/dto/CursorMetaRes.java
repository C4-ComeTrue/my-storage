package com.c4cometrue.mystorage.storage.dto;

import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;

public record CursorMetaRes(CursorFolderResponse cursorFolderResponse, CursorFileResponse cursorFileResponse) {
	public static CursorMetaRes of(CursorFolderResponse cursorFolderResponse,
		CursorFileResponse cursorFileResponse) {
		return new CursorMetaRes(cursorFolderResponse, cursorFileResponse);
	}
}
