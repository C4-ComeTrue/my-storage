package com.c4cometrue.mystorage.folder;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.util.FolderUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderReader folderReader;
	private final FolderWriter folderWriter;

	// 부모 폴더는 null 이 될 수 있다
	public void createBy(Long userId, String userFolderName, Long parentId) {
		String storedFolderName = FolderMetadata.storedName(userFolderName);
		String parentPath = folderReader.findPathBy(parentId);

		Path path = Paths.get(parentPath, storedFolderName);

		folderWriter.persist(userFolderName, storedFolderName, path.toString(), userId, parentId);
		FolderUtil.createFolder(path);
	}

	public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
		folderWriter.changeFolderNameBy(folderName, folderId, userId);
	}
}
