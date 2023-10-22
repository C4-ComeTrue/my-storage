package com.c4cometrue.mystorage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCd {
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "File not exist"), // 파일이 DB에 존재하지 않는 경우
    NO_PERMISSION(HttpStatus.FORBIDDEN, "No Permission"), // 권한이 없는 파일에 접근하는 경우
    DUPLICATE_FILE(HttpStatus.BAD_REQUEST, "Duplicate File"), // 이미 존재하는 파일인 경우
    INVALID_FILE(HttpStatus.BAD_REQUEST, "Invalid File"), // 잘못된 파일 업로드 시도
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"); // 서버 에러🤯

    private final HttpStatus httpStatus;
    private final String message;

    public ServiceException serviceException() {
        return new ServiceException(this.name(), message);
    }

    public ServiceException serviceException(String debugMessage, Object... debugMessageArgs) {
        return new ServiceException(this.name(), message, String.format(debugMessage, debugMessageArgs));
    }

    public ServiceException serviceException(Throwable cause, String debugMessage, Object... debugMessageArgs) {
        return new ServiceException(cause, this.name(), message, String.format(debugMessage, debugMessageArgs));
    }
}
