package com.c4cometrue.mystorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.c4cometrue.mystorage.entity.FolderMetaData;

public interface FolderRepository extends JpaRepository<FolderMetaData, Long> {
	Optional<FolderMetaData> findByFolderId(long folderId);

	@Query("SELECT f.parentFolderId FROM FolderMetaData f where f.folderId=:folderId")
	Long findParentFolderIdByFolderId(long folderId);

	Optional<FolderMetaData> findByFolderNameAndParentFolderIdAndUserName(String folderName, long parentFolderId,
		String userName);

	Optional<List<FolderMetaData>> findAllByParentFolderId(long parentFolderId);

	Page<FolderMetaData> findAllByParentFolderId(long parentFolderId, Pageable pageable);
}
