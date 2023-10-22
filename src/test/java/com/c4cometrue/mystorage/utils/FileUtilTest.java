package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {

    @Mock
    ResourceLoader resourceLoader;

    @InjectMocks
    FileUtil fileUtil;

    String fileUploadPath = "C://test.jpg";

    @Test
    void 파일_업로드를_성공한다() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);

        // when
        fileUtil.uploadFile(multipartFile, fileUploadPath);

        // then
        verify(multipartFile).transferTo(any(Path.class));
    }

    @Test
    void 파일_처리_과정에서_문제가_발생하면_업로드가_실패한다() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);
        doThrow(IOException.class).when(multipartFile).transferTo(any(Path.class));

        // when
        var ex = assertThrows(BusinessException.class,
                () -> fileUtil.uploadFile(multipartFile, fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_UPLOAD_FAILED, ex.getErrorCode());
    }

    @Test
    void 파일_다운로드를_성공한다() {
        // given
        var resource = mock(UrlResource.class);

        given(resourceLoader.getResource(any())).willReturn(resource);
        given(resource.getFilename()).willReturn(fileUploadPath);
        given(resource.exists()).willReturn(true);
        given(resource.isReadable()).willReturn(true);

        // when
        var downloadResource = fileUtil.downloadFile(fileUploadPath);

        // then
        assertEquals(downloadResource.getFilename(), fileUploadPath);
    }

    @Test
    void 파일이_없거나_읽을_수_없다면_다운로드가_실패한다() {
        // given
        var resource = mock(UrlResource.class);

        given(resourceLoader.getResource(any())).willReturn(resource);
        lenient().when(resource.exists()).thenReturn(false);
        lenient().when(resource.isReadable()).thenReturn(false);

        // when
        var ex = assertThrows(BusinessException.class,
                () -> fileUtil.downloadFile(fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_DOWNLOAD_FAILED, ex.getErrorCode());
    }

    @Test
    void 파일_삭제에_성공한다() {
        // given
        var filesMockedStatic = mockStatic(Files.class);

        // when
        fileUtil.deleteFile(fileUploadPath);

        // then
        filesMockedStatic.verify(() -> Files.delete(any()), times(1));
        filesMockedStatic.close();
    }

    @Test
    void 파일_처리_과정에서_문제가_발생하면_삭제가_실패한다() {
        // when
        var ex = assertThrows(BusinessException.class,
                () -> fileUtil.deleteFile(fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_DELETE_FAILED, ex.getErrorCode());
    }

}
