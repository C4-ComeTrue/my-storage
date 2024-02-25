package com.c4cometrue.mystorage.rootfile;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RootFolderRepository extends JpaRepository<RootFolderMetadata, Long> {

    boolean existsByStoredFolderName(String storedFolderName);

    boolean existsByOwnerIdAndOriginalFolderName(Long userId, String userFolderName);

    boolean existsByIdAndOwnerId(Long id, Long userId);
}