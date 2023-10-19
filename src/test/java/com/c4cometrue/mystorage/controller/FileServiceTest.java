package com.c4cometrue.mystorage.controller;

import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.service.FileService;
import com.c4cometrue.mystorage.service.StoragePathService;
import com.c4cometrue.mystorage.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @InjectMocks
    FileService fileService;
    @Mock
    FileRepository fileRepository;
    @Mock
    StoragePathService storagePathService;
    private static MockedStatic<FileUtil> fileUtilMockedStatic;

    @BeforeAll
    public static void setup() {
        fileUtilMockedStatic = mockStatic(FileUtil.class);
    }

    @AfterAll
    public static void tearDown() {
        fileUtilMockedStatic.close();
    }

    @Test
    @DisplayName("배고픔")
    void uploadFile() {
        // given
        var file = mock(MultipartFile.class);
        var userName = "userName";
        var originalFileName = "file.txt";
        var size = 100L;
        var contentType = "text/plain";

        given(file.getOriginalFilename()).willReturn(originalFileName);
        given(file.getSize()).willReturn(size);
        given(file.getContentType()).willReturn(contentType);

        // when
        var fileMetadata = fileService.uploadFile(file, userName);

        // then
        assertThat(fileMetadata)
                .matches(metadata -> StringUtils.contains(metadata.fileStorageName(), originalFileName))
                .matches(metadata -> metadata.size() == size)
                .matches(metadata -> StringUtils.equals(metadata.mime(), contentType))
                .matches(metadata -> StringUtils.equals(metadata.owner(), userName));
    }

    //    @Test
//    @DisplayName("파일 업로드 성공")
//    void uploadFileSuccess() {
//        // 파일 업로드 실행 시 동일한 CreateFileRes가 반환되는지 확인해야함
//        // 그런데 실제로 Files.copy를 하면 안됨
//        try (MockedStatic<Files> theMock = Mockito.mockStatic(Files.class)) {
//            theMock.when(() -> Files.copy(any(InputStream.class), any(Path.class))).thenReturn(1L); // 임의의 값으로 설정
//        }
//        CreateFileRes uploadedFile = fileService.uploadFile(mockMultipartFile, username);
//
//        // 파일 일치 여부 확인
//        assertEquals(createFileRes.getFileOriginalName(), uploadedFile.getFileOriginalName());
//        assertEquals(createFileRes.getSize(), uploadedFile.getSize());
//        assertEquals(createFileRes.getOwner(), uploadedFile.getOwner());
//
//        verify(fileRepository, times(1)).save(any());
//    }


//    @Test
//    @Disabled
//    @DisplayName("파일 업로드 실패 - 빈 파일")
//    void uploadFileFailEmptyFile() {
//        MockMultipartFile emptyFile = new MockMultipartFile(
//            "text.txt",
//            "",
//            "text/plain",
//            new byte[0]
//        );
//        FileException fileException = assertThrows(FileException.class, () -> fileService.uploadFile(emptyFile, username));
//        assertEquals(HttpStatus.BAD_REQUEST, fileException.getHttpStatus());
//
//        verify(fileRepository, times(0)).save(any());
//    }
//
//    @Test
//    @DisplayName("파일 삭제 성공")
//    void deleteFileSuccess() {
//        try (MockedStatic<Files> theMock = Mockito.mockStatic(Files.class)) {
//            when(fileRepository.findByFileStorageName(fileMetaData.getFileStorageName())).thenReturn(fileMetaData);
//            fileService.deleteFile(fileMetaData.getFileStorageName(), username);
//
//            verify(fileRepository, times(1)).delete(fileMetaData);
//        }
//    }
//
//    @Test
//    @DisplayName("파일 다운로드 성공")
//    void downloadFileSuccess() throws IOException {
//        when(fileRepository.findByFileStorageName(any())).thenReturn(fileMetaData);
//
//        FileDownloadRes downloadRes = fileService.downloadFile(fileMetaData.getFileStorageName(), username);
//
//        // 파일의 실제 내용이 일치 하는지
//        assertArrayEquals(fileDownloadRes.getResource().getContentAsByteArray(),
//            downloadRes.getResource().getContentAsByteArray());
//        assertEquals(fileDownloadRes.getFileMetaData().getFileName(), downloadRes.getFileMetaData().getFileName());
//        assertEquals(fileDownloadRes.getFileMetaData().getOwner(), downloadRes.getFileMetaData().getOwner());
//
//        verify(fileRepository, times(1)).findByFileStorageName(fileMetaData.getFileStorageName());
//    }
//
//    @Test
//    @DisplayName("파일 조회 실패 - 서버에 없는 파일")
//    void getFileFailWrongFileName() {
//        FileException fileException = assertThrows(FileException.class, () -> fileService.getFile("different.txt", username));
//        assertEquals(HttpStatus.BAD_REQUEST, fileException.getHttpStatus());
//        verify(fileRepository, times(1)).findByFileStorageName("different.txt");
//    }
//
//    @Test
//    @DisplayName("파일 조회 실패 - 권한이 없는 파일")
//    void getFileFailWrongUserName() {
//        String fileStorageName = fileMetaData.getFileStorageName();
//        assertNotNull(fileStorageName);
//
//        when(fileRepository.findByFileStorageName(fileStorageName)).thenReturn(fileMetaData);
//
//        FileException fileException = assertThrows(FileException.class, () -> fileService.getFile(
//            fileStorageName, "anonymous"));
//        assertEquals(HttpStatus.FORBIDDEN, fileException.getHttpStatus());
//        verify(fileRepository, times(1)).findByFileStorageName(fileMetaData.getFileStorageName());
//    }

}
