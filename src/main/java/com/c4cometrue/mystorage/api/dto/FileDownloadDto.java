package com.c4cometrue.mystorage.api.dto;

public class FileDownloadDto {

    public record Response(
        byte[] data,
        String contentType
    ){ }

}
