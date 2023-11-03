package com.c4cometrue.mystorage.file;

import java.util.List;

public interface FileReader {
	FileMetadata findBy(Long fileId, Long userId);

	List<FileMetadata> findChildBy(Long parentId, Long userId);
}
