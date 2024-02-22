package com.c4cometrue.mystorage.rootfile.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRootFolderReq(@NotNull(message = "유저id는 null이 될 수 없습니다") long userId,
                                  @NotNull(message = "폴더명은 null이 될 수 없습니다") String userFolderName) {
}
