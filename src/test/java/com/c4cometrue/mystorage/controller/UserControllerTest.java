package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.dto.request.file.SignUpReq;
import com.c4cometrue.mystorage.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	@InjectMocks
	UserController userController;
	@Mock
	UserService userService;

	@Test
	@DisplayName("회원가입")
	void signUp() {
		// given
		var req = new SignUpReq(MOCK_USER_NAME);

		// when
		userController.signUp(req);

		// then
		verify(userService, times(1)).signUp(req.userName());
	}
}
