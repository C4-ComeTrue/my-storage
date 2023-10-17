package com.c4cometrue.mystorage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
public class ApiExceptionRes {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timestmap;

    public ApiExceptionRes(String message, HttpStatus httpStatus, ZonedDateTime timestmap) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestmap = timestmap;
    }


}
