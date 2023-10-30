package com.c4cometrue.mystorage.file;

import com.c4cometrue.mystorage.file.entity.FileMetaData;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class TestMockFiles {

    public static final MultipartFile mockMultipartFile = mock(MultipartFile.class);
    public static final String mockRootPath = "/Users/sinhyeyeon/Desktop/storage/";
    public static final String mockDownRootPath = "/Users/sinhyeyeon/Desktop/storage_down/";
    public static final String mockUserName = "userName";
    public static final String mockFileName = "test.txt";
    public static final String mockFilePath = mockRootPath + UUID.randomUUID() + "." + mockFileName;
    public static final Long mockSize = 100L;
    public static final String mockContentType = "text/plain";
    public static final FileMetaData mockFileMetaData =
            FileMetaData.builder()
                    .fileName(mockFileName)
                    .savedPath(mockFilePath)
                    .fileSize(mockSize)
                    .userName(mockUserName)
                    .fileMine(mockContentType)
                    .build();
    public static final Path mockUploadPath = Paths.get(mockFilePath);
    public static final Path mockDeletePath = Paths.get(mockFilePath).toAbsolutePath();
    public static final Path mockStoragePath = Path.of(mockRootPath).resolve(mockFilePath);
    public static final Path mockDownloadPath = Path.of(mockDownRootPath).resolve(mockFileName);
}
