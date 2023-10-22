package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {

    @Test
    void 파일_업로드를_성공한다() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);
        var fileUploadPath = "C://test.jpg";

        // when
        FileUtil.uploadFile(multipartFile, fileUploadPath);

        // then
        verify(multipartFile).transferTo(any(Path.class));
    }

    @Test
    void 파일_처리_과정에서_문제가_발생하면_업로드가_실패한다() throws IOException {
        // given
        var multipartFile = mock(MultipartFile.class);
        var fileUploadPath = "C://test.jpg";
        doThrow(IOException.class).when(multipartFile).transferTo(any(Path.class));

        // when
        var ex = assertThrows(BusinessException.class,
                () -> FileUtil.uploadFile(multipartFile, fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_UPLOAD_FAILED, ex.getErrorCode());
    }

    //@Test
    void 파일_다운로드를_성공한다() {
        // given
        var resource = mock(UrlResource.class);
        var fileUploadPath = "C://test.jpg";

        ResourceLoader loader = mock(DefaultResourceLoader.class);
        given(loader.getResource(any())).willReturn(resource);

        given(resource.exists()).willReturn(true);
        given(resource.isReadable()).willReturn(true);

        // when
        var downloadResource = FileUtil.downloadFile(fileUploadPath);

        // then
        assertEquals(downloadResource.getFilename(), fileUploadPath);
    }

    //@Test
    void 파일이_없거나_읽을_수_없다면_다운로드가_실패한다() {
        // given
        var resource = mock(UrlResource.class);
        var classLoader = mock(URLClassLoader.class);
        var fileUploadPath = "C://test.jpg";

        ResourceLoader loader = mock(DefaultResourceLoader.class);
        given(loader.getClassLoader()).willReturn(classLoader);
        given(loader.getResource(any())).willReturn(resource);

        given(resource.exists()).willReturn(false);
        given(resource.isReadable()).willReturn(false);

        // when
        var ex = assertThrows(BusinessException.class,
                () -> FileUtil.downloadFile(fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_DOWNLOAD_FAILED, ex.getErrorCode());
    }

    //@Test
    void 파일_처리_과정에서_문제가_발생하면_다운로드가_실패한다() {
        // given
        var resource = mock(UrlResource.class);
        var fileUploadPath = "C://test.jpg";
        ResourceLoader loader = mock(DefaultResourceLoader.class);
        given(loader.getResource(any())).willReturn(resource);

        given(resource.exists()).willReturn(true);
        given(resource.isReadable()).willReturn(true);
        given(resource).willThrow(MalformedURLException.class);

        // when
        var ex = assertThrows(BusinessException.class,
                () -> FileUtil.downloadFile(fileUploadPath));

        // then
        assertEquals(ErrorCode.FILE_DOWNLOAD_FAILED, ex.getErrorCode());
    }

}
