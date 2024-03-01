package com.c4cometrue.mystorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.c4cometrue.mystorage.entity.DeleteLog;

public interface DeleteLogRepository extends JpaRepository<DeleteLog, Long> {
}
