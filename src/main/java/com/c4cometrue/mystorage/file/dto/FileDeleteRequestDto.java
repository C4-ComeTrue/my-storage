package com.c4cometrue.mystorage.file.dto;

public record FileDeleteRequestDto (
        String fileName,
        String userName
){
    public static FileDeleteRequestDto create (String fileName, String userName)
    {
        return new FileDeleteRequestDto(fileName, userName);
    }

}
