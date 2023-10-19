package com.c4cometrue.mystorage.util;

import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {
    private static MockedStatic<Files> fileMockedStatic;

    @BeforeAll
    public static void setup() {
        fileMockedStatic = mockStatic(Files.class);
    }

    @AfterAll
    public static void tearDown() {
        fileMockedStatic.close();
    }


    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFile() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);
        var filePath = mock(Path.class);
        var inputStream = mock(InputStream.class);

        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getInputStream()).willReturn(inputStream);

        // when
        FileUtil.uploadFile(multipartFile, filePath);

        // then
        fileMockedStatic.verify(() -> Files.copy(inputStream, filePath), times(1));
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일이 비어있는 경우")
    void uploadFile_fail1() {
        // given
        var multipartFile = mock(MultipartFile.class);
        var filePath = mock(Path.class);

        given(multipartFile.isEmpty()).willReturn(true);

        // when
        var exception = assertThrows(ServiceException.class, () -> FileUtil.uploadFile(multipartFile, filePath));

        // then
        assertEquals(ErrorCd.INVALID_FILE.name(), exception.getErrCode());
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 처리 과정에서 문제가 발생하는 경우")
    void uploadFile_fail2() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);
        var filePath = mock(Path.class);
        var inputStream = mock(InputStream.class);

        given(multipartFile.isEmpty()).willReturn(false);
        given(multipartFile.getInputStream()).willReturn(inputStream);
        given(Files.copy(inputStream, filePath)).willThrow(IOException.class);

        // when
        var exception = assertThrows(ServiceException.class, () -> FileUtil.uploadFile(multipartFile, filePath));

        // then
        assertEquals(ErrorCd.INTERNAL_SERVER_ERROR.name(), exception.getErrCode());
    }

    @Test
    void deleteFile() {
    }

    @Test
    void getFile() {
    }
}