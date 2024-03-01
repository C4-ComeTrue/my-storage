package com.c4cometrue.mystorage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCd {
    // File 관련 에러
    FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "File doesn't exist"), // 파일이 DB에 존재하지 않는 경우
    NO_PERMISSION(HttpStatus.FORBIDDEN, "No Permission"), // 권한이 없는 파일에 접근하는 경우
    DUPLICATE_FILE(HttpStatus.BAD_REQUEST, "Duplicate File"), // 이미 존재하는 파일인 경우
    INVALID_FILE(HttpStatus.BAD_REQUEST, "Invalid File"), // 잘못된 파일 업로드 시도

    // Folder 관련 에러
    FOLDER_NOT_EXIST(HttpStatus.NOT_FOUND, "Folder doesn't exist"), // 폴더가 존재하지 않는 경우
    DUPLICATE_FOLDER(HttpStatus.BAD_REQUEST, "Duplicate Folder"), // 이미 존재하는 폴더명
    FOLDER_CANT_BE_MOVED(HttpStatus.BAD_REQUEST, "folder route might have cycle"), // 하위 폴더로 이동하려는 경우

    // User 관련 에러
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, "User name Duplicate"), // 이미 존재하는 유저 이름

    // 그 외
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"); // 서버 에러

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
