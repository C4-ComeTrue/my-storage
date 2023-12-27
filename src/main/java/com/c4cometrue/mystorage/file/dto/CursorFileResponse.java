package com.c4cometrue.mystorage.file.dto;

import java.util.List;

import com.c4cometrue.mystorage.file.FileMetadata;

public record CursorFileResponse(List<FileMetadata> fileMetadata, Boolean fileHasNext) {
	public static CursorFileResponse of(List<FileMetadata> fileMetadata, Boolean fileHasNext) {
		return new CursorFileResponse(fileMetadata, fileHasNext);
	}
}
