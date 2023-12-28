package com.c4cometrue.mystorage.meta.dto;

import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;

public record CursorMetaResponse(CursorFolderResponse cursorFolderResponse, CursorFileResponse cursorFileResponse) {
	public static CursorMetaResponse of(CursorFolderResponse cursorFolderResponse,
		CursorFileResponse cursorFileResponse) {
		return new CursorMetaResponse(cursorFolderResponse, cursorFileResponse);
	}
}
