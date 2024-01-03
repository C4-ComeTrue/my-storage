package com.c4cometrue.mystorage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.domain.FileMetaData;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, Long> {

	boolean existsByFileNameAndUserIdAndParent(String fileName, long userId, FileMetaData parent);

	Optional<FileMetaData> findByIdAndUserId(long fileId, long userId);

	Optional<FileMetaData> findByUserIdAndParent(long userId, FileMetaData parent);

	List<FileMetaData> findAllByUserIdAndParent(long userId, FileMetaData parent);

}
