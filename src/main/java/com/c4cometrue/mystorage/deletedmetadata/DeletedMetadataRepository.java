package com.c4cometrue.mystorage.deletedmetadata;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeletedMetadataRepository extends JpaRepository<DeletedMetadata, Long> {
	@Query("""
        SELECT m FROM DeletedMetadata m 
        WHERE m.id > :cursorId
        ORDER BY m.id
    """)
	List<DeletedMetadata> findAllWithCursor(Long cursorId, Pageable pageable);
}
