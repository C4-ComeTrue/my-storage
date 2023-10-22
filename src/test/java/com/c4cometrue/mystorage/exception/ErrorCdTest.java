package com.c4cometrue.mystorage.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorCdTest {
    @Test
    void noArgumentConstructor() {
        var exception = ErrorCd.FILE_NOT_EXIST.serviceException();
        assertEquals("FILE_NOT_EXIST", exception.getErrCode());
        assertEquals("File not exist", exception.getErrMessage());
    }

    @Test
    void debugMessageConstructor() {
        var exception = ErrorCd.FILE_NOT_EXIST.serviceException("File %s not found", "test.txt");
        assertEquals("FILE_NOT_EXIST", exception.getErrCode());
        assertEquals("File not exist", exception.getErrMessage());
        assertEquals("File test.txt not found", exception.getDebugMessage());
    }

    @Test
    void throwConstructor() {
        var cause = mock(Throwable.class);
        var exception = ErrorCd.FILE_NOT_EXIST.serviceException(cause, "File %s not found", "test.txt");
        assertEquals("FILE_NOT_EXIST", exception.getErrCode());
        assertEquals("File not exist", exception.getErrMessage());
        assertEquals("File test.txt not found", exception.getDebugMessage());
    }

    @Test
    void fileNotExist() {
        assertEquals(HttpStatus.NOT_FOUND, ErrorCd.FILE_NOT_EXIST.getHttpStatus());
        assertEquals("File not exist", ErrorCd.FILE_NOT_EXIST.getMessage());
    }

    @Test
    void noPermission() {
        assertEquals(HttpStatus.FORBIDDEN, ErrorCd.NO_PERMISSION.getHttpStatus());
        assertEquals("No Permission", ErrorCd.NO_PERMISSION.getMessage());
    }

    @Test
    void duplicateFile() {
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCd.DUPLICATE_FILE.getHttpStatus());
        assertEquals("Duplicate File", ErrorCd.DUPLICATE_FILE.getMessage());
    }

    @Test
    void invalidFile() {
        assertEquals(HttpStatus.BAD_REQUEST, ErrorCd.INVALID_FILE.getHttpStatus());
        assertEquals("Invalid File", ErrorCd.INVALID_FILE.getMessage());
    }

    @Test
    void internalServerError() {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCd.INTERNAL_SERVER_ERROR.getHttpStatus());
        assertEquals("Internal Server Error", ErrorCd.INTERNAL_SERVER_ERROR.getMessage());
    }

}
