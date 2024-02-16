package com.c4cometrue.mystorage.util;

import com.c4cometrue.mystorage.exception.ErrorCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderUtil {
    private FolderUtil() {
        throw new AssertionError("should not be invoke");
    }

    public static void createFolder(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw ErrorCode.FOLDER_CREATE_ERROR.serviceException();
        }
    }
}
