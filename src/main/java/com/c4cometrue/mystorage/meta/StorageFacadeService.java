package com.c4cometrue.mystorage.meta;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.meta.dto.CursorMetaResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageFacadeService {
	private final FolderService folderService;
	private final FileService fileService;
	private final PagingHelper pagingHelper;

	public CursorMetaResponse getFolderContents(Long parentId, Long cursorId, Long userId, Integer size,
		boolean cursorFlag) {
		Integer contentsSize = pagingHelper.calculateSize(size);

		if (cursorFlag) {
			return handleFolderFirstStrategy(parentId, cursorId, userId, contentsSize);
		} else {
			return handleFileFirstStrategy(parentId, cursorId, userId, contentsSize);
		}
	}

	private CursorMetaResponse handleFolderFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = pagingHelper.createPageable(contentsSize);
		CursorFolderResponse cursorFolderResponse = folderService.getFolders(parentId, cursorId, userId, page);

		if (Boolean.FALSE.equals(cursorFolderResponse.folderHasNext())) {
			Pageable remainPage = pagingHelper.createPageable(
				contentsSize - cursorFolderResponse.folderMetadata().size());
			CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, null, userId, remainPage);
			return CursorMetaResponse.of(cursorFolderResponse, cursorFileResponse);
		}
		return CursorMetaResponse.of(cursorFolderResponse, new CursorFileResponse(null, false));
	}

	private CursorMetaResponse handleFileFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = pagingHelper.createPageable(contentsSize);
		CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, cursorId, userId, page);
		return CursorMetaResponse.of(CursorFolderResponse.of(null, false), cursorFileResponse);
	}
}
