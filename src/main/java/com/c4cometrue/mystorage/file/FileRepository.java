package com.c4cometrue.mystorage.file;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Metadata, Long> {
}
