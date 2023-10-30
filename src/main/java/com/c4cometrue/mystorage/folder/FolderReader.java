package com.c4cometrue.mystorage.folder;

public interface FolderReader {
	String findPathBy(Long parentId);

	FolderMetadata findBy(Long parentId);

	void verifyBy(Long parentId, Long userId);

	void checkDuplicateBy(String userFolderName, Long parentId, Long userId);
}
