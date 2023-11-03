package com.c4cometrue.mystorage.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record FileUploadRequest(@NotNull(message = "파일을 전달해주세요") MultipartFile multipartFile,
								@NotNull(message = "사용자 id는 null 될 수 없습니다") long userId,
								Long parentId) {
	public static FileUploadRequest of(MultipartFile multipartFile, long userId, Long parentId) {
		return new FileUploadRequest(multipartFile, userId, parentId);
	}
}
