package com.c4cometrue.mystorage.util;

import java.util.Arrays;

public enum FileType {
    IMAGE("image"),
    VIDEO("video"),
    AUDIO("audio"),
    DOCUMENT("application"),
    OTHER("other");

    private final String mine;

    FileType(String mine) {
        this.mine = mine;
    }

    public static FileType classifyType(String mine) {
        return Arrays.stream(FileType.values())
            .filter(fileType -> mine.startsWith(fileType.mine))
            .findFirst()
            .orElse(OTHER);
    }
}
