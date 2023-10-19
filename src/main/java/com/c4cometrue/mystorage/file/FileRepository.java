package com.c4cometrue.mystorage.file;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Metadata, Long> {
	Optional<Metadata> findByIdAndUploaderId(Long fileId, Long userId);
}
