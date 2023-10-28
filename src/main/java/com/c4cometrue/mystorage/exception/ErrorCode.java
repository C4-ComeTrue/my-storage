package com.c4cometrue.mystorage.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor //
public enum ErrorCode {

    FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."), // 요청한 파일이 존재하지 않을 때
    FILE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "해당 요청에 대한 권한이 없습니다."), // 다운로드, 삭제 요청에 대한 권한이 없는 사용자일 때
    FILE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."), // 기타 잘못된 요청
    FILE_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 이름의 파일이 이미 존재합니다."),
    FILE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리과정에서 문제가 발생했습니다.");

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
