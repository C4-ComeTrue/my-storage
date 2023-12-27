package com.c4cometrue.mystorage.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileDownloadRequest(
		@NotNull(message = "파일 id는 null 될 수 없습니다") long fileId,
		@NotBlank(message = "경로를 지정해주셔야 합니다") String userPath,
		@NotNull(message = "사용자 id는 null 될 수 없습니다") long userId) {
	public static FileDownloadRequest of(long fileId, String userPath, long userId) {
		return new FileDownloadRequest(fileId, userPath, userId);
	}
}
