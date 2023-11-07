package com.c4cometrue.mystorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.c4cometrue.mystorage.entity.FolderMetaData;

@Repository
public interface FolderRepository extends JpaRepository<FolderMetaData, Long> {
	Optional<FolderMetaData> findByFolderId(long folderId);

	@Query("SELECT f.folderPath FROM FolderMetaData f WHERE f.folderId = :folderId")
	Optional<String> findFolderPathByFolderId(long folderId);

	Optional<FolderMetaData> findByUserNameAndFolderNameAndParentFolderId(String userName, String folderName,
		long parentFolderId);

	Optional<List<FolderMetaData>> findAllByParentFolderId(long parentFolderId);
}
