package com.c4cometrue.mystorage.controller;

import static com.c4cometrue.mystorage.controller.TestParameter.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.exception.FileException;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.service.FileService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    FileRepository fileRepository;

    FileService fileService;

    @BeforeEach
    public void setUp() {
        fileService = new FileService(rootPath, fileRepository) {
            // 실제 파일 생성 Mocking
            @Override
            protected Resource createResource(Path storagePath) {
                return fileResource;
            }
        };
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFileSuccess() {
        // 파일 업로드 실행 시 동일한 CreateFileRes가 반환되는지 확인해야함
        // 그런데 실제로 Files.copy를 하면 안됨
        try (MockedStatic<Files> theMock = Mockito.mockStatic(Files.class)) {
            theMock.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L); // 임의의 값으로 설정
        }
        CreateFileRes uploadedFile = fileService.uploadFile(mockMultipartFile, username);

        // 파일 일치 여부 확인
        assertEquals(createFileRes.getFileOriginalName(), uploadedFile.getFileOriginalName());
        assertEquals(createFileRes.getSize(), uploadedFile.getSize());
        assertEquals(createFileRes.getOwner(), uploadedFile.getOwner());

        verify(fileRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("파일 업로드 실패 - 빈 파일")
    void uploadFileFailEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "text.txt",
            "",
            "text/plain",
            new byte[0]
        );
        FileException fileException = assertThrows(FileException.class, () -> fileService.uploadFile(emptyFile, username));
        assertEquals(HttpStatus.BAD_REQUEST, fileException.getHttpStatus());

        verify(fileRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFileSuccess() {
        try (MockedStatic<Files> theMock = Mockito.mockStatic(Files.class)) {
            when(fileRepository.findByFileStorageName(fileMetaData.getFileStorageName())).thenReturn(fileMetaData);
            fileService.deleteFile(fileMetaData.getFileStorageName(), username);

            verify(fileRepository, times(1)).delete(fileMetaData);
        }
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadFileSuccess() throws IOException {
        when(fileRepository.findByFileStorageName(any())).thenReturn(fileMetaData);

        FileDownloadRes downloadRes = fileService.downloadFile(fileMetaData.getFileStorageName(), username);

        // 파일의 실제 내용이 일치 하는지
        assertArrayEquals(fileDownloadRes.getResource().getContentAsByteArray(),
            downloadRes.getResource().getContentAsByteArray());
        assertEquals(fileDownloadRes.getFileMetaData().getFileName(), downloadRes.getFileMetaData().getFileName());
        assertEquals(fileDownloadRes.getFileMetaData().getOwner(), downloadRes.getFileMetaData().getOwner());

        verify(fileRepository, times(1)).findByFileStorageName(fileMetaData.getFileStorageName());
    }

    @Test
    @DisplayName("파일 조회 실패 - 서버에 없는 파일")
    void getFileFailWrongFileName() {
        FileException fileException = assertThrows(FileException.class, () -> fileService.getFile("different.txt", username));
        assertEquals(HttpStatus.BAD_REQUEST, fileException.getHttpStatus());
        verify(fileRepository, times(1)).findByFileStorageName("different.txt");
    }

    @Test
    @DisplayName("파일 조회 실패 - 권한이 없는 파일")
    void getFileFailWrongUserName() {
        String fileStorageName = fileMetaData.getFileStorageName();
        assertNotNull(fileStorageName);

        when(fileRepository.findByFileStorageName(fileStorageName)).thenReturn(fileMetaData);

        FileException fileException = assertThrows(FileException.class, () -> fileService.getFile(
            fileStorageName, "anonymous"));
        assertEquals(HttpStatus.FORBIDDEN, fileException.getHttpStatus());
        verify(fileRepository, times(1)).findByFileStorageName(fileMetaData.getFileStorageName());
    }

}
