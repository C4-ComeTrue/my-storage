package com.c4cometrue.mystorage.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ErrorCodeTest {

    @Test
    void errorMessageTest() {
        var exception = ErrorCode.FILE_NOT_EXIST.serviceException();

        assertEquals("FILE_NOT_EXIST", exception.getErrorCode());
        assertEquals("존재하지 않는 파일입니다.", exception.getErrorMessage());
    }

    @Test
    void debugMessageTest() {
        var exception = ErrorCode.FILE_NOT_EXIST.serviceException("File [%s] is not found", "test.txt");

        assertEquals("FILE_NOT_EXIST", exception.getErrorCode());
        assertEquals("존재하지 않는 파일입니다.", exception.getErrorMessage());
        assertEquals("File [test.txt] is not found", exception.getDebugMessage());
    }

    @Test
    void throwTest() {
        var cause = mock(Throwable.class);
        var exception = ErrorCode.FILE_NOT_EXIST.serviceException(cause, "File [%s] is not found", "test.txt");

        assertEquals("FILE_NOT_EXIST", exception.getErrorCode());
        assertEquals("존재하지 않는 파일입니다.", exception.getErrorMessage());
        assertEquals("File [test.txt] is not found", exception.getDebugMessage());
    }
}
