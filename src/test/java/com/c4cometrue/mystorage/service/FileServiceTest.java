package com.c4cometrue.mystorage.service;

import static com.c4cometrue.mystorage.TestParameter.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.util.FileUtil;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @InjectMocks
    FileService fileService;
    @Mock
    FileRepository fileRepository;
    @Mock
    StoragePathService storagePathService;
    @Mock
    ResourceLoader mockResourceLoader;

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
    @DisplayName("파일 업로드 성공")
    void uploadFile() {
        // given
        given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
        given(mockMultipartFile.getSize()).willReturn(mockSize);
        given(mockMultipartFile.getContentType()).willReturn(mockContentType);

        // when
        var createFileRes = fileService.uploadFile(mockMultipartFile, mockUserName);
        // then
        assertThat(createFileRes)
                .matches(metadata -> StringUtils.equals(
                    StringUtils.substring(metadata.fileStorageName(), 36), mockFileName))
                .matches(metadata -> metadata.size() == mockSize)
                .matches(metadata -> StringUtils.equals(metadata.mime(), mockContentType))
                .matches(metadata -> StringUtils.equals(metadata.username(), mockUserName));
    }

    @Test
    @DisplayName("파일 업로드 실패 - 중복 파일명")
    void uploadFileFailDuplicateName() {
        // given
        given(mockMultipartFile.getOriginalFilename()).willReturn(mockFileName);
        given(fileRepository.findByFileNameAndUsername(mockFileName, mockUserName))
            .willReturn(Optional.of(new FileMetaData()));

        // when
        var exception = assertThrows(ServiceException.class,
            () -> fileService.uploadFile(mockMultipartFile, mockUserName));

        // then
        assertEquals(ErrorCd.DUPLICATE_FILE.name(), exception.getErrCode());
    }

    @Test
    @DisplayName("파일 데이터 DB 확인")
    void getFileMetaData() {
        // given
        given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

        // when
        var fileMetadata = fileService.getFileMetaData(mockFileStorageName, mockUserName);

        // then
        assertThat(fileMetadata)
            .matches(metadata -> StringUtils.equals(metadata.getFileName(), mockFileName))
            .matches(metadata -> metadata.getSize() == mockSize)
            .matches(metadata -> StringUtils.equals(metadata.getMime(), mockContentType))
            .matches(metadata -> StringUtils.equals(metadata.getUsername(), mockUserName));
    }

    @Test
    @DisplayName("파일 데이터 DB 확인 실패 - 파일 없음")
    void getFileMetaDataFailWrongFileStorageName() {
        // given
        var wrongFileStorageName = "wrong_file_path.file";
        given(fileRepository.findByFileStorageName(wrongFileStorageName)).willReturn(Optional.empty());

        // when
        var exception = assertThrows(ServiceException.class,
            () -> fileService.getFileMetaData(wrongFileStorageName, mockUserName));

        // then
        assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
    }

    @Test
    @DisplayName("파일 데이터 DB 확인 실패 - 요청자가 주인이 아님")
    void getFileMetaDataFailNotOwner() {
        // given
        given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

        // when
        var exception = assertThrows(ServiceException.class,
            () -> fileService.getFileMetaData(mockFileStorageName, "anonymous"));

        // then
        assertEquals(ErrorCd.NO_PERMISSION.name(), exception.getErrCode());
    }

    @Test
    @DisplayName("파일 삭제")
    void deleteFile() {
        // given
        given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));

        // when
        fileService.deleteFile(mockFileStorageName, mockUserName);

        // then
        verify(storagePathService, times(1)).createTotalPath(any());
        verify(fileRepository, times(1)).findByFileStorageName(any());
        verify(fileRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("파일 다운로드")
    void downloadFile() {
        // given
        var mockResource = mock(Resource.class);

        given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));
        given(storagePathService.createTotalPath(any())).willReturn(mockStoragePath);
        given(mockResourceLoader.getResource(any())).willReturn(mockResource);
        given(mockResource.exists()).willReturn(true);

        // when
        fileService.downloadFile(mockFileStorageName, mockUserName);

        // then
        verify(storagePathService, times(1)).createTotalPath(any());
        verify(fileRepository, times(1)).findByFileStorageName(any());
    }

    @Test
    @DisplayName("파일 다운로드 실패")
    void downloadFileFail() {
        // given
        var mockResource = mock(Resource.class);

        given(fileRepository.findByFileStorageName(mockFileStorageName)).willReturn(Optional.of(mockFileMetaData));
        given(storagePathService.createTotalPath(any())).willReturn(mockStoragePath);
        given(mockResourceLoader.getResource(any())).willReturn(mockResource);
        given(mockResource.exists()).willReturn(false);  // 물리적 파일을 찾지 못함

        // when
        var exception = assertThrows(ServiceException.class,
            () -> fileService.downloadFile(mockFileStorageName, mockUserName));

        // then
        verify(storagePathService, times(1)).createTotalPath(any());
        verify(fileRepository, times(1)).findByFileStorageName(any());
        assertEquals(ErrorCd.FILE_NOT_EXIST.name(), exception.getErrCode());
    }

}
