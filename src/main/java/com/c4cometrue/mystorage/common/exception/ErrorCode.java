package com.c4cometrue.mystorage.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// common
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
	BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일이 존재하지 않습니다."),

	// file-upload
	FILE_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
	DUPLICATE_FILE(HttpStatus.BAD_REQUEST, "이미 존재하는 파일입니다."),

	// file-download
	INVALID_FILE_ACCESS(HttpStatus.FORBIDDEN, "다운로드할 파일에 접근할 권한이 없습니다."),
	FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다."),

	// file-delete
	FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),

	// folder-create
	FOLDER_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "폴더 생성에 실패했습니다."),
	DUPLICATE_FOLDER(HttpStatus.BAD_REQUEST, "이미 존재하는 이름의 폴더입니다."),
	INVALID_FOLDER(HttpStatus.BAD_REQUEST, "유효하지 않은 폴더입니다.");

	private final HttpStatus httpStatus;

	private final String msg;

}