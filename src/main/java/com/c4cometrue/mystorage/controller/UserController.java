package com.c4cometrue.mystorage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.request.SignUpReq;
import com.c4cometrue.mystorage.dto.response.SignUpRes;
import com.c4cometrue.mystorage.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SignUpRes signUp(@Valid @RequestBody SignUpReq signUpReq) {
		return userService.signUp(signUpReq);
	}
}
