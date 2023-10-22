package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c4cometrue.mystorage.service.FileService;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
    @InjectMocks
    private FileController fileController;
    @Mock
    private FileService fileService;

    @Test
    @DisplayName("파일 업로드")
    void uploadFile() {
        // then
        fileController.uploadFile(mockMultipartFile, mockUserName);

        // 테스트 결과 검증
        verify(fileService, times(1)).uploadFile(mockMultipartFile, mockUserName);
    }

    @Test
    @DisplayName("파일 삭제")
    void deleteFile() {
        // then
        fileController.deleteFile(mockStoragePath.toString(), mockUserName);

        // 테스트 결과 검증
        verify(fileService, times(1)).deleteFile(mockStoragePath.toString(), mockUserName);
    }

    @Test
    @DisplayName("파일 다운로드")
    void downloadFile() {
        // then
        fileController.downloadFile(mockStoragePath.toString(), mockUserName);

        // 테스트 결과 검증
        verify(fileService, times(1)).downloadFile(mockStoragePath.toString(), mockUserName);
    }
}
