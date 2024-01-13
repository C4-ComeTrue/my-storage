package com.c4cometrue.mystorage.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
@DisplayName("베이스엔티티 테스트")
class MetadataBaseEntityTest {

	private MetadataBaseEntity entity;

	@BeforeEach
	public void setUp() {
		entity = new MetadataBaseEntity();
	}

	@Test
	@DisplayName("베이스 엔티티 저장 전 날짜 세팅 테스트")
	void prePersistTest() {
		entity.prePersist();

		LocalDate now = LocalDate.now();
		assertEquals(now, entity.getCreatedAt());
		assertEquals(now, entity.getUpdatedAt());
	}

	@Test
	@DisplayName("베이스 엔티티 업데이트 테스트")
	void preUpdateTest() {
		entity.preUpdate();

		assertEquals(LocalDate.now(), entity.getUpdatedAt());
	}
}
