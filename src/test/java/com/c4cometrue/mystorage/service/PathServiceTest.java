package com.c4cometrue.mystorage.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PathServiceTest {

	PathService pathService;

	String baseDir = "C://root/";

	@BeforeEach
	public void setUp() {
		pathService = new PathService();
		ReflectionTestUtils.setField(pathService,
			"baseDir",
			baseDir);
	}

	@Test
	void 루트_폴더의_물리적인_경로를_반환한다() {
		// given
		var parentDir = "";
		var fileName = "name";

		// when
		var response = pathService.getFullFilePath(parentDir, fileName);

		// then
		assertThat(response)
			.matches(path -> path.equals(baseDir + fileName));
	}

	@Test
	void 파일의_물리적인_경로를_반환한다() {
		// given
		var parentDir = "/test";
		var fileName = " name ";

		// when
		var response = pathService.getFullFilePath(parentDir, fileName);

		// then
		assertThat(response)
			.matches(path -> path.equals(baseDir + "/test/name"));

	}
}
