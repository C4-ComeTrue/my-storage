package com.c4cometrue.mystorage.file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<FileMetadata, Long> {
	Optional<FileMetadata> findByIdAndUploaderId(Long id, Long uploaderId);

	@Query("SELECT CASE WHEN COUNT(m) > 0 " + "THEN TRUE " + "ELSE FALSE END " + "FROM FileMetadata m " + "WHERE "
		+ "(m.parentId = :parentId OR (m.parentId IS NULL AND :parentId IS NULL)) " + "AND m.uploaderId = :uploaderId "
		+ "AND m.originalFileName = :fileName ")
	boolean checkDuplicateFileName(Long parentId, Long uploaderId, String fileName);

	List<FileMetadata> findByParentIdAndUploaderId(Long parentId, Long userId);

	Boolean existsByIdAndUploaderId(Long parentId, Long userId);

	List<FileMetadata> findAllByParentIdAndUploaderIdOrderByIdDesc(Long parentId, Long uploaderId, Pageable page);

	List<FileMetadata> findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(Long parentId, Long userId, Long cursorId,
		Pageable pageable);

	Boolean existsByParentIdAndUploaderIdAndIdLessThan(Long parentId, Long uploaderId, Long id);

	List<FileMetadata> findAllByParentId(Long parentId);
	@Query("SELECT m.status "
		+ "FROM FileMetadata m ")
	List<FileMetadata> findAllWithCursor(Long cursorId, Pageable pageable);
}
