package com.c4cometrue.mystorage.folder;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<FolderMetadata, Long> {

	boolean existsByParentIdAndUploaderIdAndOriginalFolderName(Long parentId, Long userId, String folderName);

	boolean existsByIdAndUploaderId(Long folderId, Long userId);

	List<FolderMetadata> findByParentIdAndUploaderId(Long parentId, Long userId);

	Boolean existsByParentIdAndUploaderIdAndIdLessThan(Long parentId, Long userId, Long id);

	List<FolderMetadata> findAllByParentIdAndUploaderIdOrderByIdDesc(Long parentId, Long userId, Pageable page);

	List<FolderMetadata> findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(Long parentId, Long userId,
		Long cursorId, Pageable page);
}
