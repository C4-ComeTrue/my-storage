package com.c4cometrue.mystorage.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED_FILE_ACCESS(HttpStatus.FORBIDDEN, "비정상적인 요청입니다."),

    CANNOT_FOUND_FILE(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),

    FILE_COPY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사 중 오류가 발생했습니다."),

    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),

    DUPLICATE_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 업로드에 중복이 발생 했습니다"),

    FOLDER_CREATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "폴더 생성 중 오류가 발생했습니다"),
    UNAUTHORIZED_FOLDER_ACCESS(HttpStatus.FORBIDDEN, "비정상적인 요청입니다."),
    DUPLICATE_FOLDER_NAME(HttpStatus.BAD_REQUEST, "폴더 업로드에 중복이 발생 했습니다"),
    DUPLICATE_SERVER_FOLDER_NAME(HttpStatus.BAD_REQUEST, "폴더 UUID 중복이 발생 했습니다"),
    CANNOT_FOUND_FOLDER(HttpStatus.NOT_FOUND, "해당 폴더를 찾을 수 없습니다."),

    DUPLICATE_BASE_PATH(HttpStatus.BAD_REQUEST, "기본 경로 생성에 중복이 발생했습니다"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 맴버를 찾지 못했습니다"),
    EXCEEDED_CAPACITY(HttpStatus.INSUFFICIENT_STORAGE, "더 이상 업로드 할 수 없습니다"),
    INVALID_OPERATION(HttpStatus.BAD_REQUEST, "사용 중인 공간보다 많은 공간은 해제할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;

    public ServiceException serviceException() {
        return new ServiceException(this.name(), message);
    }

    public ServiceException serviceException(String debugMessage, Object... debugMessageArgs) {
        return new ServiceException(this.name(), message, String.format(debugMessage, debugMessageArgs));
    }
}
