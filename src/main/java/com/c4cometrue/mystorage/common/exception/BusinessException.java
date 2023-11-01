package com.c4cometrue.mystorage.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	static final long serialVersionUID = -8294120478345891911L;

	private final ErrorCode errorCode;
	private final String debugMessage;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMsg());
		this.errorCode = errorCode;
		this.debugMessage = null;
	}

	public BusinessException(ErrorCode errorCode, Exception ex) {
		super(errorCode.getMsg(), ex);
		this.errorCode = errorCode;
		this.debugMessage = null;
	}

	public BusinessException(ErrorCode errorCode, String debugMessage) {
		super(errorCode.getMsg());
		this.errorCode = errorCode;
		this.debugMessage = debugMessage;
	}

	public BusinessException(ErrorCode errorCode, String debugMessage, Exception ex) {
		super(errorCode.getMsg(), ex);
		this.errorCode = errorCode;
		this.debugMessage = debugMessage;
	}

}
