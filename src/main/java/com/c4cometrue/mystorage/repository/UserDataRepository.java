package com.c4cometrue.mystorage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.c4cometrue.mystorage.entity.UserData;

@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {
	Optional<UserData> findByUserName(String username);
}
