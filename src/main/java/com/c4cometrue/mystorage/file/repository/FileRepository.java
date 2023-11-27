package com.c4cometrue.mystorage.file.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.file.entity.FileMetaData;

public interface FileRepository extends JpaRepository<FileMetaData, UUID> {
	Optional<FileMetaData> findByFileNameAndUserName(String fileName, String userName);

	Optional<FileMetaData> findByFileName(String fileName);
}
