package com.c4cometrue.mystorage.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
	UNAUTHORIZED_FILE_ACCESS(HttpStatus.FORBIDDEN, "비정상적인 요청입니다."),

	CANNOT_FOUND_FILE(HttpStatus.NOT_FOUND, "해당 파일을 찾을 수 없습니다."),

	FILE_COPY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사 중 오류가 발생했습니다."),

	FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 중 오류가 발생했습니다."),

	DUPLICATE_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 업로드에 중복이 발생 했습니다");

	private final HttpStatus httpStatus;
	private final String message;
}
