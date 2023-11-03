package com.c4cometrue.mystorage.folder;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderMetadata, Long> {

	boolean existsByParentIdAndUploaderIdAndOriginalFolderName(Long parentId, Long userId, String folderName);

	boolean existsByIdAndUploaderId(Long folderId, Long userId);

	List<FolderMetadata> findByParentIdAndUploaderId(Long parentId, Long userId);
}
