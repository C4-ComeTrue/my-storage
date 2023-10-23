package com.c4cometrue.mystorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.domain.FileMetaData;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {

    boolean existsByFileNameAndUserId(String fileName, long userId);

}
