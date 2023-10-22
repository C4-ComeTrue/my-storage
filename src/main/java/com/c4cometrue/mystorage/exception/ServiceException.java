package com.c4cometrue.mystorage.exception;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
	private final String errCode; // 에러 구분용 코드
	private final String errMessage; // 에러 세부 메시지
	private final String debugMessage; // 디버그 메시지 (사용자에게 노출되지 않고, 서버 로그에만 뜸)

	public ServiceException(String errCode, String errMessage) {
		super(getDetailExceptionMessage(errCode, errMessage, null));
		this.errCode = errCode;
		this.errMessage = errMessage;
		this.debugMessage = null;
	}

	public ServiceException(String errCode, String errMessage, String debugMessage) {
		super(getDetailExceptionMessage(errCode, errMessage, debugMessage));
		this.errCode = errCode;
		this.errMessage = errMessage;
		this.debugMessage = debugMessage;
	}

	public ServiceException(Throwable cause, String errCode, String errMessage, String debugMessage) {
		super(getDetailExceptionMessage(errCode, errMessage, debugMessage), cause);
		this.errCode = errCode;
		this.errMessage = errMessage;
		this.debugMessage = debugMessage;
	}

	private static String getDetailExceptionMessage(String errCode, String errMessage, String debugMessage) {
		var sb = new StringBuilder()
			.append(errCode)
			.append(" : ")
			.append(errMessage);

		if (StringUtils.isNotEmpty(debugMessage)) {
			sb.append(" - ").append(debugMessage);
		}

		return sb.toString();
	}
}
