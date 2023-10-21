package com.c4cometrue.mystorage.file;

public interface FileWriter {
	void persist(Metadata metadata, Long userId);

	void deleteBy(Long fileId);
}
