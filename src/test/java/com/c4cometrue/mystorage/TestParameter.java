package com.c4cometrue.mystorage;

import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.request.FileReq;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;

public class TestParameter {
    public static final MultipartFile mockMultipartFile = mock(MultipartFile.class);
    public static final String mockUserName = "userName";
    public static final String mockFileName = "file.txt";
    public static final String mockFileStorageName = UUID.randomUUID() + mockFileName;
    public static final long mockSize = 100L;
    public static final String mockContentType = "text/plain";
    public static final String mockRootPath = "C:/Users/pear/study/C4/storage";
    public static final Path mockStoragePath = Path.of(mockRootPath).resolve(mockFileStorageName);
}
