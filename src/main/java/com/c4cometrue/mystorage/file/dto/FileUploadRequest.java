package com.c4cometrue.mystorage.file.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record FileUploadRequest(
        @NotNull(message = "빈 값은 올 수 없습니다") MultipartFile multipartFile,
        @NotNull(message = "사용자 id는 null 될 수 없습니다") long userId,
        @NotNull(message = "폴더 id는 null 될 수 없습니다") long parentId,
        @NotNull(message = "루트 폴더 id는 null 될 수 없습니다") long rootId) {
    public static FileUploadRequest of(MultipartFile multipartFile, long userId, long parentId, long rootId) {
        return new FileUploadRequest(multipartFile, userId, parentId, rootId);
    }
}
