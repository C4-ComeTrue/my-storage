package com.c4cometrue.mystorage.file.repository;

import com.c4cometrue.mystorage.file.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileMetaData, UUID> {
    Optional<FileMetaData> findByFileNameAndUserName(String fileName, String userName);
    Optional<FileMetaData> findByFileName(String fileName);
}
