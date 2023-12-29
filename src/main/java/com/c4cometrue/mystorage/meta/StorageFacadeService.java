package com.c4cometrue.mystorage.meta;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.FolderMetadata;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.meta.dto.CursorMetaResponse;
import com.c4cometrue.mystorage.util.PagingUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageFacadeService {
	private final FolderService folderService;
	private final FileService fileService;

	public CursorMetaResponse getFolderContents(Long parentId, Long cursorId, Long userId, Integer size,
		boolean cursorFlag) {
		Integer contentsSize = PagingUtil.calculateSize(size);

		return cursorFlag
			? handleFolderFirstStrategy(parentId, cursorId, userId, contentsSize)
			: handleFileFirstStrategy(parentId, cursorId, userId, contentsSize);
	}

	private CursorMetaResponse handleFolderFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = PagingUtil.createPageable(contentsSize);
		CursorFolderResponse cursorFolderResponse = folderService.getFolders(parentId, cursorId, userId, page);

		if (Boolean.FALSE.equals(cursorFolderResponse.folderHasNext())) {
			Pageable remainPage = PagingUtil.createPageable(
				contentsSize - cursorFolderResponse.folderMetadata().size());
			CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, null, userId, remainPage);
			return CursorMetaResponse.of(cursorFolderResponse, cursorFileResponse);
		}
		return CursorMetaResponse.of(cursorFolderResponse, new CursorFileResponse(null, false));
	}

	private CursorMetaResponse handleFileFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = PagingUtil.createPageable(contentsSize);
		CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, cursorId, userId, page);
		return CursorMetaResponse.of(CursorFolderResponse.of(null, false), cursorFileResponse);
	}

	public void deleteFolderContents(long folderId, long userId) {
		// 유효성 검사
		folderService.validateBy(folderId, userId);

		FolderMetadata folderMetadata = folderService.findBy(folderId, userId);

		deleteFolderContents(folderMetadata);
	}

	// softDelete
	@Transactional
	public void deleteFolderContents(FolderMetadata folderMetadata) {
		// 하위 폴더 재귀적으로 약한 삭제
		folderService.findAllBy(folderMetadata.getId())
			.forEach(this::deleteFolderContents);
		// 폴더에 속한 파일 약한 삭제
		fileService.findAllBy(folderMetadata.getParentId())
			.forEach(FileMetadata::deleteFile);
		// 현재 폴더 약한 삭제
		folderService.deleteFile(folderMetadata);
	}
}
