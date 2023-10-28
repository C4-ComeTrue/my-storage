package com.c4cometrue.mystorage.exception;

import java.time.ZonedDateTime;
public record ErrorResponse (String message, ZonedDateTime timestamp){
}
