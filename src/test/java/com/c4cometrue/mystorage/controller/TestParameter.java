package com.c4cometrue.mystorage.controller;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

public class TestParameter {
    public static MockMultipartFile mockMultipartFile = new MockMultipartFile(
        "test.txt",
        "test.txt",
        "text/plain",
        "C4 ComeTrue 저장소 과제".getBytes()
    );
    public static String username = "HA_EUN";
    public static FileMetaData fileMetaData = FileMetaData.builder()
        .fileName(mockMultipartFile.getName())
        .fileStorageName(UUID.randomUUID()+mockMultipartFile.getOriginalFilename())
        .size(mockMultipartFile.getSize())
        .mime(mockMultipartFile.getContentType())
        .owner(username)
        .build();

    public static Resource fileResource = new AbstractResource() {
        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(mockMultipartFile.getBytes());
        }
    };

    public static FileDownloadRes fileDownloadRes = FileDownloadRes.builder()
        .fileMetaData(fileMetaData)
        .resource(fileResource)
        .build();
    public static CreateFileRes createFileRes = new CreateFileRes(fileMetaData);

    public static String rootPath = "C:/Users/pear/study/C4/storage";
    public static Path storagePath = Path.of(rootPath);

}
