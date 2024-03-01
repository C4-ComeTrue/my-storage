package com.c4cometrue.mystorage.file.dto;

import jakarta.validation.constraints.NotNull;

public record FileDeleteRequest(
        @NotNull(message = "파일 id는 null 될 수 없습니다") long fileId,
        @NotNull(message = "사용자 id는 null 될 수 없습니다") long userId,
        @NotNull(message = "루트 폴더 id는 null 될 수 없습니다") long rootId) {
    public static FileDeleteRequest of(long fileId, long userId, long rootId) {
        return new FileDeleteRequest(fileId, userId, rootId);
    }
}
