package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoragePathServiceTest {
    StoragePathService storagePathService;

    @BeforeEach
    void setUp() {
        storagePathService = new StoragePathService(mockRootPath);
    }


    @Test
    @DisplayName("폴더 기본 경로 생성")
    void createBasicFolderPath() {
        // when
        var path = storagePathService.createBasicFolderPath(mockUserName);

        // then
        assertEquals(Path.of(mockRootPath).resolve(mockUserName), path);
    }

    @Test
    @DisplayName("폴더 경로 생성")
    void createFolderPath() {
        // given
        var mockFolderName = "my_folder";
        var parentFolderPath = Path.of(mockRootPath).resolve(mockUserName);
        var mockFolderPath = parentFolderPath.resolve(mockFolderName);

        // when
        var folderPath = storagePathService.createFolderPath(parentFolderPath.toString(), mockFolderName);

        // then
        assertEquals(mockFolderPath, folderPath);
    }
}
