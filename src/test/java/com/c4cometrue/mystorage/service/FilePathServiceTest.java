package com.c4cometrue.mystorage.service;


import com.c4cometrue.mystorage.file.service.FilePathService;
import com.c4cometrue.mystorage.file.util.FileUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.c4cometrue.mystorage.TestMockFile.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FilePathServiceTest {

    @InjectMocks
    FilePathService filePathService;

    private static MockedStatic<FileUtil> fileUtilMockedStatic;

    @BeforeAll // 매번 테스트 시작 전 초기화 작업
    public static void init() {
        fileUtilMockedStatic = mockStatic(FileUtil.class);
    }

    @AfterAll
    public static void down() {
        fileUtilMockedStatic.close();
    }

    @Test
    @DisplayName("UUID가 포함된 파일명")
    void createSavedPathTest(String fileName) {
        // 목적 : fileName만 입력되었을 때, 실제 저장할 dirpath + uuid + filename으로 구성해서 리턴
        given(filePathService.createSavedPath(mockFileName)).willReturn(mockFilePath);
//        var savedPath = mockRootPath + UUID.randomUUID() + "." + mockFileName;

//        assertEquals(mockFilePath.substring(mockFilePath.lastIndexOf(".", -1)), mockFileName);
//        assertEquals(filePathService.createSavedPath(mockFileName), mockFilePath);
    }

}
