package com.c4cometrue.mystorage.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FileDeleteDto {

	public record Request(
		@NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Positive Long userId,
		@NotNull(message = "파일 ID는 null이 될 수 없습니다.") @Positive Long fileId
	) {

	}
}
