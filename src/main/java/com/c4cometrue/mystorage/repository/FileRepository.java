package com.c4cometrue.mystorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.entity.FileMetaData;

public interface FileRepository extends JpaRepository<FileMetaData, Long> {
	Optional<FileMetaData> findByFolderIdAndUserNameAndFileName(long folderId, String username, String filename);

	Optional<List<FileMetaData>> findAllByFolderId(long folderId);

	Page<FileMetaData> findAllByFolderId(long folderId, Pageable pageable);
}
