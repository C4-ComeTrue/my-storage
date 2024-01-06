package com.c4cometrue.mystorage.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PagingUtil {
	private PagingUtil()  {
		throw new AssertionError("should not be invoke");
	}
	private static final int DEFAULT_SIZE = 10;

	public static Integer calculateSize(Integer size) {
		return size == null ? DEFAULT_SIZE : size;
	}

	public static Pageable createPageable(int size) {
		return PageRequest.of(0, size);
	}
}