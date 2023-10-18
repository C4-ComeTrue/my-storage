package com.c4cometrue.mystorage.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FileUploadRequest(@NotBlank(message = "파일을 전달해주세요") MultipartFile multipartFile,
								@NotNull(message = "사용자 id는 null 될 수 없습니다") Long userId) {
	public static FileUploadRequest of(MultipartFile multipartFile, Long userId) {
		return new FileUploadRequest(multipartFile, userId);
	}
}
