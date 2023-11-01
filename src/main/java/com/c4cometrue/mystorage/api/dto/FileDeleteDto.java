package com.c4cometrue.mystorage.api.dto;

import jakarta.validation.constraints.NotNull;

public class FileDeleteDto {

	public record Request(
		@NotNull(message = "유저 ID는 null이 될 수 없습니다.") Long userId,
		@NotNull(message = "파일 ID는 null이 될 수 없습니다.") Long fileId
	) {

	}
}
