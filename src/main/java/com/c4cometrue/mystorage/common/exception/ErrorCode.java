package com.c4cometrue.mystorage.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "파일 업로드에 실패하였습니다."),
    DUPLICATE_FILE(HttpStatus.BAD_REQUEST, "이미 존재하는 파일입니다.");

    private final HttpStatus httpStatus;

    private final String message;

}
