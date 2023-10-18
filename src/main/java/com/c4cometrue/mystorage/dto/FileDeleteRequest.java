package com.c4cometrue.mystorage.dto;

import jakarta.validation.constraints.NotNull;

public record FileDeleteRequest (
								@NotNull(message = "파일 id는 null 될 수 없습니다") Long fileId,
								 @NotNull(message = "사용자 id는 null 될 수 없습니다") Long userId) {
	public static FileDeleteRequest of (Long fileId, Long userId){
		return new FileDeleteRequest(fileId, userId);
	}
}
