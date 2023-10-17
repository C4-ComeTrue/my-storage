package com.c4cometrue.mystorage.repository;

import com.c4cometrue.mystorage.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileMetaData, Long> {
    FileMetaData findByFileStorageName(String fileStorageName);
}
