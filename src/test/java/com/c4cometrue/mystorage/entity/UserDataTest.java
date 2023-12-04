package com.c4cometrue.mystorage.entity;

import static org.assertj.core.api.Assertions.*;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class UserDataTest {

	@Test
	void builderTest() {
		// given
		var userName = "userName";

		// when
		var userMetaData = new UserData(userName);

		// then
		assertThat(userMetaData)
			.matches(metadata -> StringUtils.equals(metadata.getUserName(), userName));
	}

}
