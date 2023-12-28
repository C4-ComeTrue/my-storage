package com.c4cometrue.mystorage.folder.dto;

import jakarta.validation.constraints.NotNull;

// Long parentId, Long cursorId, Long userId, Integer size, boolean cursorFlag
public record FolderContentsRequest(
	Long parentId,
	Long cursorId,
	@NotNull(message = "사용자 id는 널이 될 수 없습니다") long userId,
	Integer size,
	boolean cursorFlag

) {
	public static FolderContentsRequest of(Long parentId, Long cursorId, long userId, Integer size,
		boolean cursorFlag) {
		return new FolderContentsRequest(parentId, cursorId, userId, size, cursorFlag);
	}
}
