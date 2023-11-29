package com.c4cometrue.mystorage.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FolderRenameDto {

	public record Req(
		long userId,
		@NotNull(message = "폴더 ID는 Null이 될 수 없습니다.") Long folderId,
		@NotBlank(message = "폴더 이름은 빈 문자열이 될 수 없습니다.") String name
	) {

	}
}
