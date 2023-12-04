package com.c4cometrue.mystorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.entity.FolderMetaData;

public interface FolderRepository extends JpaRepository<FolderMetaData, Long> {
	Optional<FolderMetaData> findByFolderId(long folderId);

	Optional<FolderMetaData> findByFolderNameAndParentFolderIdAndUserName(String folderName, long parentFolderId,
		String userName);

	Optional<List<FolderMetaData>> findAllByParentFolderId(long parentFolderId);
}
