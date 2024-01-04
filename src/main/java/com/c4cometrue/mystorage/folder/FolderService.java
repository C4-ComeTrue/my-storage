package com.c4cometrue.mystorage.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.folder.dto.FolderContent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderDataHandlerService folderDataHandlerService;

	// 부모 폴더는 null 이 될 수 있다
	public void createBy(Long userId, String userFolderName, Long parentId) {
		String storedFolderName = FolderMetadata.storedName(userFolderName);
		String parentPath = findPathBy(parentId, userId);

		Path path = Paths.get(parentPath, storedFolderName);

		folderDataHandlerService.persist(userFolderName, storedFolderName, path.toString(), userId, parentId);
		// FolderUtil.createFolder(path);
	}

	public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
		folderDataHandlerService.changeFolderNameBy(folderName, folderId, userId);
	}

	public String findPathBy(Long parentId, Long userId) {
		return folderDataHandlerService.findPathBy(parentId, userId);
	}

	public CursorFolderResponse getFolders(Long parentId, Long cursorId, Long userId, Pageable page) {
		List<FolderMetadata> folders = folderDataHandlerService.getFolderList(parentId, cursorId, userId, page);
		List<FolderContent> folderContents = folders.stream()
			.map(folder -> FolderContent.of(folder.getId(), folder.getOriginalFolderName()))
			.toList();
		Long lastIdOfList = folders.isEmpty() ? null : folders.get(folders.size() - 1).getId();
		return CursorFolderResponse.of(folderContents,
			folderDataHandlerService.hasNext(parentId, userId, lastIdOfList));
	}

	public void validateBy(Long folderId, Long userId) {
		folderDataHandlerService.validateFolderOwnershipBy(folderId, userId);
	}

	public void moveFolder(Long folderId, Long userId, Long destinationFolderId) {
		// 해당 폴더가 접근 가능 한 폴더 인지 체크
		validateBy(destinationFolderId, userId);

		FolderMetadata folderMetadata = folderDataHandlerService.findBy(folderId, userId);

		folderMetadata.changeParentId(destinationFolderId);

		folderDataHandlerService.persist(folderMetadata);

	}

	public void deleteFile(FolderMetadata folderMetadata) {
		folderMetadata.deleteFolder();
		// folderDataHandlerService.persist(folderMetadata);
	}

	public List<FolderMetadata> findAllBy(Long parentId) {
		return folderDataHandlerService.findAllBy(parentId);
	}

	public FolderMetadata findBy(long folderId, long userId) {
		return folderDataHandlerService.findBy(folderId, userId);
	}
}
