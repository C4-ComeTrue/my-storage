package com.c4cometrue.mystorage.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Boolean existsByBasePath(String basePath);
}
