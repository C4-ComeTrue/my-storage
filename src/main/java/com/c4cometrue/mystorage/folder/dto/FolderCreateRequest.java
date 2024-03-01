package com.c4cometrue.mystorage.folder.dto;

import jakarta.validation.constraints.NotNull;

public record FolderCreateRequest(
    @NotNull long userId,
    @NotNull String userFolderName,
    Long parentId,
    @NotNull long rootId) {
    public static FolderCreateRequest of(long userId, String userFolderName, Long parentId, long rootId) {
        return new FolderCreateRequest(userId, userFolderName, parentId, rootId);
    }
}
