package com.c4cometrue.mystorage.file.service;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.file.entity.FileMetaData;
import com.c4cometrue.mystorage.file.repository.FileRepository;
import com.c4cometrue.mystorage.file.util.FileUtil;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Optional;

import static com.c4cometrue.mystorage.file.TestMockFiles.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @InjectMocks
    FileService fileService;

    @Mock
    FileRepository fileRepository;

    @Mock
    FileUtil fileUtil;

    @Mock
    FilePathService filePathService;

    @Mock
    ResourceLoader mockResourceLoader;

    private static MockedStatic<FileUtil> fileUtilMockedStatic;

    @BeforeAll // 매번 테스트 시작 전 초기화 작업
    public static void init() {
        fileUtilMockedStatic = mockStatic(FileUtil.class);
    }

    @AfterAll
    public static void down() {
        fileUtilMockedStatic.close();
    }

    @DisplayName("파일업로드 성공 테스트")
    @Test
    void fileUploadSuccessTest() {
        given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
        given(mockMultipartFile.getSize()).willReturn(mockSize);
        given(mockMultipartFile.getContentType()).willReturn(mockContentType);

        fileService.fileUpload(mockMultipartFile, mockUserName);

        assertThat(mockFileMetaData)
                .matches(metadata -> mockFileMetaData.getUserName().equals(mockUserName))
                .matches(metadata -> mockFileMetaData.getFileName().equals(mockFileName));
    }

    @DisplayName("파일업로드 실패 테스트")
    @Test
    void fileUploadFailTest() {
        given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
        given(fileRepository.findByFileNameAndUserName(mockFileName, mockUserName))
                .willReturn(Optional.of(new FileMetaData()));

        ServiceException se = assertThrows(ServiceException.class,
                () -> fileService.fileUpload(mockMultipartFile, mockUserName));

        assertEquals(ErrorCode.FILE_IS_DUPLICATED.name(), se.getErrorCode());
    }

    // @DisplayName("파일다운로드 성공")
    // @Test
    // void fileDownloadTest() throws IOException {
    //     var inputStream = mock(InputStream.class);
    //     var outputStream = mock(OutputStream.class);
    //     var files = mockStatic(Files.class);
    //
    //     given(fileRepository.findByFileName(mockFileName)).willReturn(Optional.of(mockFileMetaData));
    //
    //     given(inputStream.read(any()))
    //             .willReturn(10)
    //             .willReturn(20)
    //             .willReturn(30)
    //             .willReturn(-1);
    //
    //     given(Files.newOutputStream(mockDownloadPath)).willReturn(outputStream);
    //
    //     fileService.fileDownload(
    //             mockFileMetaData.getFileName(),
    //             mockFileMetaData.getUserName(),
    //             mockDownRootPath);
    //
    //     then(outputStream).should(times(3))
    //             .write(any(), eq(0), anyInt());
    //
    //     files.close();
    // }

    @DisplayName("파일다운로드 실패")
    @Test
    void fileDownloadFailTest() throws IOException {
        // given
        var inputStream = mock(InputStream.class);
        var outputStream = mock(OutputStream.class);
        var files = mockStatic(Files.class);

        var otherUserName = "otherUser";
        var otherFileName = "otherFileName";

        given(fileRepository.findByFileName(mockFileName)).willReturn(Optional.of(mockFileMetaData));
        given(Files.newOutputStream(mockDownloadPath)).willReturn(outputStream);

        var permissionException = assertThrows(ServiceException.class,
                () -> fileService.fileDownload(mockFileName, otherUserName, mockDownRootPath));

        var notFoundException = assertThrows(ServiceException.class,
                () -> fileService.fileDownload(otherFileName, mockUserName, mockDownRootPath));

        assertEquals(ErrorCode.FILE_PERMISSION_DENIED.name(), permissionException.getErrorCode());
        assertEquals(ErrorCode.FILE_NOT_EXIST.name(), notFoundException.getErrorCode());
    }

    @DisplayName("파일삭제 성공 테스트")
    @Test
    void fileDeleteSuccessTest() {
        // given
        given(fileRepository.findByFileName(mockFileName)).willReturn(Optional.of(mockFileMetaData));

        // when
        fileService.fileDelete(mockFileMetaData.getFileName(), mockFileMetaData.getUserName());

        // then
        verify(fileRepository, times(1)).delete(any());
    }
    // 로컬에서 소나큐브 관련 시스템 깔고
    // 그 시스템에 , 병철이가설정한 환경변수, build.gradle의 환경변수
    // 그래서 시스템을 이용해서미리, code smell이런거를 확인 , 고치고,
    // 그다음에 pr을 올리는건가..

    //

    @DisplayName("파일삭제 실패 테스트")
    @Test
    void fileDeleteFailTest() {
        // given
        var otherUserName = "otherUserName";
        var otherFileName = "otherFileName";

        given(fileRepository.findByFileName(mockFileName)).willReturn(Optional.of(mockFileMetaData));

        // when
        var permissionException = assertThrows(ServiceException.class,
                () -> fileService.fileDelete(
                        mockFileName,
                        otherUserName
                ));

        var notFoundException = assertThrows(ServiceException.class,
                () -> fileService.fileDelete(
                        otherFileName,
                        mockUserName
                ));

        // then
        assertEquals(ErrorCode.FILE_PERMISSION_DENIED.name(), permissionException.getErrorCode());
        assertEquals(ErrorCode.FILE_NOT_EXIST.name(), notFoundException.getErrorCode());
    }
}
