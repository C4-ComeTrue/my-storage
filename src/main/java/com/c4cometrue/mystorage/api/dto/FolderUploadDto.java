package com.c4cometrue.mystorage.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FolderUploadDto {

	public record Req(
		@NotNull(message = "유저 ID는 Null이 될 수 없습니다.") Long userId,
		@NotNull(message = "부모 폴더 ID는 Null이 될 수 없습니다.") Long parentId,
		@NotBlank(message = "폴더 이름은 빈 문자열이 될 수 없습니다.") String name
	) {
	}

	public record Res(
		Long id
	) {
	}
}