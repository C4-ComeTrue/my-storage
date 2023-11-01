package com.c4cometrue.mystorage.common.exception.handler;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.common.exception.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handle(BusinessException ex) {
		log.error(ex.getMessage(), ex);

		if (!Objects.isNull(ex.getDebugMessage())) {
			log.debug(ex.getDebugMessage());
		}

		val errorCode = ex.getErrorCode();
		val errorResponse = new ErrorResponse(errorCode.getMsg());
		return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handle(BindException ex) {
		log.error(ex.getMessage(), ex);
		val errors = ex.getBindingResult().getFieldErrors().stream()
			.map(ErrorResponse.ErrorDetail::from)
			.toList();

		val errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST_ERROR.getMsg(), errors);
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler
	public ResponseEntity<Object> handle(Exception ex) {
		log.error(ex.getMessage(), ex);
		return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.getMsg());
	}

}
