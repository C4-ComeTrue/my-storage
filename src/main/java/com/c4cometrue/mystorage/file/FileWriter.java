package com.c4cometrue.mystorage.file;

public interface FileWriter {
	void persist(Metadata metadata);

	void deleteBy(Long fileId);
}
