package com.c4cometrue.mystorage;

import static org.mockito.Mockito.mock;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class TestParameter {
    public static final MultipartFile MOCK_MULTIPART_FILE = mock(MultipartFile.class);
    public static final String MOCK_USER_NAME = "userName";
    public static final String MOCK_FILE_NAME = "file.txt";
    public static final String MOCK_FILE_STORAGE_NAME = UUID.randomUUID() + MOCK_FILE_NAME;
    public static final long MOCK_SIZE = 100L;
    public static final String MOCK_CONTENT_TYPE = "text/plain";
    public static final String MOCK_ROOT_PATH = "C:/Users/pear/study/C4/storage";
    public static final Path MOCK_STORAGE_PATH = Path.of(MOCK_ROOT_PATH).resolve(MOCK_FILE_STORAGE_NAME);
}
