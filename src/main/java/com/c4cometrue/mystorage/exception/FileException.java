package com.c4cometrue.mystorage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileException extends RuntimeException {
    private final HttpStatus httpStatus;

    public FileException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
