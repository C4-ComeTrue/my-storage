package com.c4cometrue.mystorage.storage;

import com.c4cometrue.mystorage.common.MetadataBaseEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		assertEquals(now, entity.getCreatedAt());
		assertEquals(now, entity.getUpdatedAt());
	}

	@Test
	@DisplayName("베이스 엔티티 업데이트 테스트")
	void preUpdateTest() {
		entity.preUpdate();

		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		assertTrue(now.minusMinutes(1).isBefore(entity.getUpdatedAt())
				&& now.plusMinutes(1).isAfter(entity.getUpdatedAt()));
	}
}
