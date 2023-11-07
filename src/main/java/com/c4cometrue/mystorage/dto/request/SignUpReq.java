package com.c4cometrue.mystorage.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link com.c4cometrue.mystorage.entity.UserData}
 */
public record SignUpReq(@NotBlank(message = "user name is blank") String userName) {
}
