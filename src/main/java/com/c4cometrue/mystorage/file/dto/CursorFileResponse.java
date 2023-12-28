package com.c4cometrue.mystorage.file.dto;

import java.util.List;

public record CursorFileResponse(List<FileContent> fileContents, Boolean fileHasNext) {
	public static CursorFileResponse of(List<FileContent> fileContents, Boolean fileHasNext) {
		return new CursorFileResponse(fileContents, fileHasNext);
	}
}
