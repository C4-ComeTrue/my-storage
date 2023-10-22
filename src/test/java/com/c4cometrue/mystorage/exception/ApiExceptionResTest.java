package com.c4cometrue.mystorage.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class ApiExceptionResTest {

    @Test
    void createApiExceptionRes() {
        ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ApiExceptionRes apiExceptionRes = new ApiExceptionRes("Test message", timestamp);

        assertEquals("Test message", apiExceptionRes.message());
        assertEquals(timestamp, apiExceptionRes.timestamp());
    }
}
