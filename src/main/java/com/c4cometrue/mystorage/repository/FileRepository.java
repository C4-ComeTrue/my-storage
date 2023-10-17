package com.c4cometrue.mystorage.repository;

import com.c4cometrue.mystorage.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
