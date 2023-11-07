package com.c4cometrue.mystorage.entity;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class UserTest {

	@Test
	void builderTest() {
		// given
		var userName = "userName";

		// when
		var userMetaData = UserData.builder()
			.userName(userName)
			.build();

		// then
		assertThat(userMetaData)
			.matches(metadata -> metadata.getUserId() == 0)
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), userName));
	}

}
