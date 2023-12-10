package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

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
        storagePathService = new StoragePathService(MOCK_ROOT_PATH);
    }


    @Test
    @DisplayName("사용자별 기본 폴더 생성")
    void createBasicFolderPath() {
        // when
        var path = storagePathService.createPathByUser(MOCK_USER_NAME);

        // then
        assertEquals(Paths.get(MOCK_ROOT_PATH, MOCK_USER_NAME), path);
    }
}
