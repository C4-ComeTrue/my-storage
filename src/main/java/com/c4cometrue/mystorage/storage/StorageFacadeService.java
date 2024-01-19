package com.c4cometrue.mystorage.storage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.deletedmetadata.DeleteMetadataService;
import com.c4cometrue.mystorage.deletedmetadata.DeletedMetadata;
import com.c4cometrue.mystorage.file.FileMetadata;
import com.c4cometrue.mystorage.file.FileService;
import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.folder.FolderMetadata;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.storage.dto.CursorMetaRes;
import com.c4cometrue.mystorage.util.PagingUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageFacadeService {
	private final FolderService folderService;
	private final FileService fileService;
	private final DeleteMetadataService deleteMetadataService;

	public CursorMetaRes getFolderContents(Long parentId, Long cursorId, Long userId, Integer size,
		boolean cursorFlag) {
		Integer contentsSize = PagingUtil.calculateSize(size);

		return cursorFlag
			? handleFolderFirstStrategy(parentId, cursorId, userId, contentsSize)
			: handleFileFirstStrategy(parentId, cursorId, userId, contentsSize);
	}

	private CursorMetaRes handleFolderFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = PagingUtil.createPageable(contentsSize);
		CursorFolderResponse cursorFolderResponse = folderService.getFolders(parentId, cursorId, userId, page);

		if (Boolean.FALSE.equals(cursorFolderResponse.folderHasNext())) {
			Pageable remainPage = PagingUtil.createPageable(
				contentsSize - cursorFolderResponse.folderMetadata().size());
			CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, null, userId, remainPage);
			return CursorMetaRes.of(cursorFolderResponse, cursorFileResponse);
		}
		return CursorMetaRes.of(cursorFolderResponse, new CursorFileResponse(null, false));
	}

	private CursorMetaRes handleFileFirstStrategy(Long parentId, Long cursorId, Long userId,
		Integer contentsSize) {
		Pageable page = PagingUtil.createPageable(contentsSize);
		CursorFileResponse cursorFileResponse = fileService.getFiles(parentId, cursorId, userId, page);
		return CursorMetaRes.of(CursorFolderResponse.of(null, false), cursorFileResponse);
	}

	@Transactional
	public void deleteFolderContents(long folderId, long userId) {
		// 유효성 검사
		folderService.validateBy(folderId, userId);

		FolderMetadata folderMetadata = folderService.findBy(folderId, userId);

		deleteFolderContents(folderMetadata);
	}

	// softDelete
	private void deleteFolderContents(FolderMetadata folderMetadata) {
		// 삭제 할 폴더리스트 조회
		List<FolderMetadata> folderMetadataList = folderService.findAllBy(folderMetadata.getId());
		// 삭제 할 파일리스트 조회
		List<FileMetadata> fileMetadataList = fileService.findAllBy(folderMetadata.getId());

		List<DeletedMetadata> deletedMetadataList = new ArrayList<>(
			createDeletedMetadata(folderMetadataList, fileMetadataList));

		// 삭제 메타베이스로 삭제할 파일,폴더 이관
		// 삭제 메타베이스는 실제 hardDelete 수행되는 파일만 저장된다
		deleteMetadataService.persist(deletedMetadataList);

		// 파일 일괄 삭제 softDelete
		fileService.deleteAll(fileMetadataList);
		// 재귀적으로 폴더 삭제
		folderMetadataList.forEach(this::deleteFolderContents);


		folderService.deleteFolder(folderMetadata);
	}

	private List<DeletedMetadata> createDeletedMetadata(List<FolderMetadata> folderMetadataList,
		List<FileMetadata> fileMetadataList) {
		List<DeletedMetadata> deletedMetadataList = new ArrayList<>();
		deletedMetadataList.addAll(createDeletedMetadataByFolders(folderMetadataList));
		deletedMetadataList.addAll(createDeletedMetadataByFiles(fileMetadataList));
		return deletedMetadataList;
	}

	private List<DeletedMetadata> createDeletedMetadataByFolders(List<FolderMetadata> folderMetadataList) {
		return folderMetadataList.stream()
			.map(metadata -> DeletedMetadata.builder()
				.userId(metadata.getUploaderId())
				.parentId(metadata.getParentId())
				.type(metadata.getMetadataType())
				.filePath(metadata.getFilePath())
				.build())
			.toList();
	}

	private List<DeletedMetadata> createDeletedMetadataByFiles(List<FileMetadata> fileMetadataList) {
		return fileMetadataList.stream()
			.map(metadata -> DeletedMetadata.builder()
				.userId(metadata.getUploaderId())
				.parentId(metadata.getParentId())
				.type(metadata.getMetadataType())
				.filePath(metadata.getFilePath())
				.build())
			.toList();
	}
}
