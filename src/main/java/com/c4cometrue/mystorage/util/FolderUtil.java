package com.c4cometrue.mystorage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.c4cometrue.mystorage.exception.ErrorCode;

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
