package com.c4cometrue.mystorage.meta;

import com.c4cometrue.mystorage.meta.dto.CursorMetaResponse;

public interface StorageStrategy {
	CursorMetaResponse getContents(Long parentId, Long cursorId, Long userId, Integer contentsSize);
}
