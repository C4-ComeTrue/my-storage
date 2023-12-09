package com.c4cometrue.mystorage.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.util.FolderUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderDataHandlerService folderDataHandlerService;

	// 부모 폴더는 null 이 될 수 있다
	public void createBy(Long userId, String userFolderName, Long parentId) {
		String storedFolderName = FolderMetadata.storedName(userFolderName);
		String parentPath = findPathBy(parentId);

		Path path = Paths.get(parentPath, storedFolderName);

		folderDataHandlerService.persist(userFolderName, storedFolderName, path.toString(), userId, parentId);
		FolderUtil.createFolder(path);
	}

	public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
		folderDataHandlerService.changeFolderNameBy(folderName, folderId, userId);
	}

	public String findPathBy(Long parentId) {
		return folderDataHandlerService.findPathBy(parentId);
	}

	public List<FolderMetadata> findChildBy(Long parentId, Long userId) {
		return folderDataHandlerService.findChildBy(parentId, userId);
	}
}
