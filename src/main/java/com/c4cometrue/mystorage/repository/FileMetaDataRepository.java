package com.c4cometrue.mystorage.repository;

import com.c4cometrue.mystorage.domain.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {

    boolean existsByFileNameAndUserId(String fileName, long userId);

}
