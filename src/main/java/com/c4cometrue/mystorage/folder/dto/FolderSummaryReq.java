package com.c4cometrue.mystorage.folder.dto;

public record FolderSummaryReq(long folderId, long userId) {
    public static FolderSummaryReq of(long folderId, long userId) {
        return new FolderSummaryReq(folderId, userId);
    }
}
