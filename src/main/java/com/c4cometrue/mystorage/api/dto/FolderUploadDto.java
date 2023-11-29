package com.c4cometrue.mystorage.api.dto;

import jakarta.validation.constraints.NotBlank;

public class FolderUploadDto {

	public record Req(
		long userId,
		long parentId,
		@NotBlank(message = "폴더 이름은 빈 문자열이 될 수 없습니다.") String name
	) {
	}

	public record Res(
		Long id
	) {
	}
}
