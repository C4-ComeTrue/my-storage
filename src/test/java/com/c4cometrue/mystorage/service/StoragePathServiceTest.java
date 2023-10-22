package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
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
    void createTotalPath() {
        // when
        var path = storagePathService.createTotalPath(mockFileStorageName);

        // then
        assertEquals(mockStoragePath, path);
    }
}
