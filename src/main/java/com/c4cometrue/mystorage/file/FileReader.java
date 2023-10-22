package com.c4cometrue.mystorage.file;

public interface FileReader {
	Metadata findBy(Long fileId, Long userId);

	void existBy(Long fileId);

	void duplicateBy(String fileName, Long userId);
}
