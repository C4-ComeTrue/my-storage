package com.c4cometrue.mystorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileDownloadRequest(
	@NotNull(message = "파일 id는 null 될 수 없습니다") Long fileId,
	@NotBlank(message = "경로를 지정해주셔야 합니다") String userPath,
	@NotNull(message = "사용자 id는 null 될 수 없습니다") Long userId) {
	public static FileDownloadRequest of(Long fileId, String userPath, Long userId) {
		return new FileDownloadRequest(fileId, userPath, userId);
	}
}
