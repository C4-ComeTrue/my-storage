package com.c4cometrue.mystorage.folder;

public interface FolderWriter {
	void persist(String userFolderName, String storedFolderName, String path, Long userId, Long parentId);
}
