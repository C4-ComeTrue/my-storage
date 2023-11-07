package com.c4cometrue.mystorage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
@Getter
public class UserData {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;

	@NotBlank(message = "user name is blank")
	private String userName;

	@Builder
	public UserData(String userName) {
		this.userName = userName;
	}
}
