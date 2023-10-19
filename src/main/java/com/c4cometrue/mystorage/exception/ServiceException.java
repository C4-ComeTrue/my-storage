package com.c4cometrue.mystorage.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 12391939123913929L;
	private final ErrorCode code;

	public ServiceException(ErrorCode errorCode) {
		this.code = errorCode;
	}
}
