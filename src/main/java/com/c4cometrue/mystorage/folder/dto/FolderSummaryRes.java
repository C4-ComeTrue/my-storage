package com.c4cometrue.mystorage.folder.dto;

import java.time.ZonedDateTime;

public record FolderSummaryRes(
        String folderName,
        ZonedDateTime createAt,
        ZonedDateTime updateAt
) {
    public static FolderSummaryRes of(String folderName, ZonedDateTime createAt, ZonedDateTime updateAt) {
        return new FolderSummaryRes(folderName, createAt, updateAt);
    }
}
