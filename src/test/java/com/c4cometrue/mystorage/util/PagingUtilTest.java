package com.c4cometrue.mystorage.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("페이징 유틸 테스트")
@ExtendWith(MockitoExtension.class)
class PagingUtilTest {
	@Test
	@DisplayName("페이지 사이즈 테스트")
	void calculateSizeTest() {
		Assertions.assertEquals(5, PagingUtil.calculateSize(5));
	}
	@Test
	@DisplayName("페이지 사이즈 테스트 널일때")
	void calculateSizeNullTest() {
		Assertions.assertEquals(10, PagingUtil.calculateSize(null));
	}

	@Test
	@DisplayName("페이지 생성 태스트")
	void createPageableTest() {
		Assertions.assertEquals(PageRequest.of(0, 10), PagingUtil.createPageable(10));
	}
}
