package com.c4cometrue.mystorage.folder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderMetadata, Long> {
	boolean existsByParentIdAndUserId(Long parentId, Long userId);

	boolean existsByParentIdAndUserIdAndOriginalFolderName(Long parentId, Long userId, String folderName);

	boolean existsByIdAndUserId(Long folderId, Long userId);
}
