package com.c4cometrue.mystorage.exception;

public record ErrorResponse(
	String errorCode,
	String errorMessage
) {
	public static ErrorResponse from(ServiceException exception) {
		return new ErrorResponse(exception.getErrCode(), exception.getErrMessage());
	}
}
