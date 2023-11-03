package com.c4cometrue.mystorage.file;

public interface FileWriter {
	void persist(FileMetadata fileMetadata, Long userId, Long parentId);

	void deleteBy(Long fileId);
}
