package com.c4cometrue.mystorage.exception;

import java.time.ZonedDateTime;

public record ApiExceptionRes (
    String message,
    ZonedDateTime timestamp
) {}
