package com.c4cometrue.mystorage.meta;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.meta.dto.CursorMetaResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class FileFirstStrategy implements StorageStrategy {
	private final FileService fileService;
	private final PagingHelper pagingHelper;

	@Override
	public CursorMetaResponse getContents(Long parentId, Long cursorId, Long userId, Integer contentsSize) {
		Pageable page = pagingHelper.createPageable(contentsSize);
		CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, cursorId, userId, page);
		return CursorMetaResponse.of(CursorFolderResponse.of(null, false), cursorFileResponse);
	}
}
