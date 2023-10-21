package com.c4cometrue.mystorage.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ServiceExceptionHandler {
	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ErrorResponse> handleServiceException(ServiceException serviceException) {
		ErrorResponse errorResponse = ErrorResponse.from(serviceException);
		ErrorCode errorCode = ErrorCode.valueOf(serviceException.getErrCode());
		return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
	}
}
