package com.c4cometrue.mystorage.rootfile.dto;

import jakarta.validation.constraints.NotNull;

public record RootInfoReq(
        @NotNull(message = "루트 id는 null이 될 수 없습니다") long rootId,
        @NotNull(message = "유저 id는 null이 될 수 없습니다") long userId
) {
}
