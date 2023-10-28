package com.c4cometrue.mystorage.exception;

import org.apache.commons.lang3.StringUtils;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{

    private String errorCode;
    private String errorMessage;
    private String debugMessage;

    public ServiceException(String errorCode, String errorMessage) {
        super(getDetailExceptionMessage(errorCode, errorMessage, null));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.debugMessage = null;
    }

    public ServiceException(String errorCode, String errorMessage, String debugMessage) {
        super(getDetailExceptionMessage(errorCode, errorMessage, debugMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.debugMessage = debugMessage;
    }

    public ServiceException(Throwable cause, String errorCode, String errorMessage, String debugMessage) {
        super(getDetailExceptionMessage(errorCode, errorMessage, debugMessage), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.debugMessage = debugMessage;
    }

    private static String getDetailExceptionMessage(String errorCode, String errorMessage, String debugMessage) {
        var sb = new StringBuffer()
                .append(errorCode)
                .append(" : ")
                .append(errorMessage);

        if(StringUtils.isNotEmpty(debugMessage)) {
            sb.append("-" + debugMessage);
        }
        return sb.toString();
    }
}
