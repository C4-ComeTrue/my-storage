package com.c4cometrue.mystorage.file;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<Metadata, Long> {
	Optional<Metadata> findByIdAndUploaderId(Long id, Long uploaderId);

	@Query("SELECT CASE WHEN COUNT(m) > 0 "
		+ "THEN TRUE "
		+ "ELSE FALSE END "
		+ "FROM Metadata m "
		+ "WHERE m.uploaderId = :uploaderId "
		+ "AND m.originalFileName = :fileName "
		+ "ORDER BY m.id DESC")
	boolean checkDuplicateFileName(String fileName, Long uploaderId);

}
