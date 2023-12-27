package com.c4cometrue.mystorage.meta;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
class PagingHelper {
	private static final int DEFAULT_SIZE = 10;

	public Integer calculateSize(Integer size) {
		return size == null ? DEFAULT_SIZE : size;
	}

	public Pageable createPageable(int size) {
		return PageRequest.of(0, size);
	}
}
