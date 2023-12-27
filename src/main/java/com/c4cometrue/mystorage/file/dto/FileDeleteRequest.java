package com.c4cometrue.mystorage.file.dto;

import jakarta.validation.constraints.NotNull;

public record FileDeleteRequest(
		@NotNull(message = "파일 id는 null 될 수 없습니다") long fileId,
		@NotNull(message = "사용자 id는 null 될 수 없습니다") long userId) {
	public static FileDeleteRequest of(long fileId, long userId) {
		return new FileDeleteRequest(fileId, userId);
	}
}
