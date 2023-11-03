package com.c4cometrue.mystorage.folder;

import java.util.List;

public interface FolderReader {
	String findPathBy(Long parentId);

	List<FolderMetadata> findChildBy(Long parentId, Long userId);
}
