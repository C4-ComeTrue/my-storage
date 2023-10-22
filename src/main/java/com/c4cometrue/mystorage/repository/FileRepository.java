package com.c4cometrue.mystorage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.c4cometrue.mystorage.entity.FileMetaData;

@Repository
public interface FileRepository extends JpaRepository<FileMetaData, Long> {
    Optional<FileMetaData> findByFileNameAndUsername(String filename, String username);

    Optional<FileMetaData> findByFileStorageName(String fileStorageName);
}
