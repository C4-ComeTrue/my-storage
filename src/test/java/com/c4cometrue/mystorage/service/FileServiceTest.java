package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    static MockedStatic<FileUtil> fileUtilMockedStatic;

    @Autowired FileService fileService;

    @BeforeAll
    static void setUp() {
        fileUtilMockedStatic = mockStatic(FileUtil.class);
    }

    @Test
    void 파일이_없다면_실패한다() {
        //given
        long userId = 1;

        // when + then
        assertThatThrownBy(() -> {
            fileService.fileUpload(null, userId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FILE_EMPTY.getMessage());
    }

    @Test
    void 중복된_파일이라면_실패한다() {
        // given
        var userId = 1L;
        var file = mock(MultipartFile.class);

        given(file.getOriginalFilename()).willReturn("dd.jpg");
        given(file.getSize()).willReturn(1000L);
        given(file.getContentType()).willReturn("text/plain");

        fileService.fileUpload(file, userId);

        // when + then
        assertThatThrownBy(() -> {
            fileService.fileUpload(file, userId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_FILE.getMessage());
    }

    @Test
    void 파일_메타데이터를_등록한다() {
        // given
        var userId = 1L;
        var file = mock(MultipartFile.class);
        var originFileName = "dd.jpg";
        var size = 1000L;
        var contentType = "text/plain";

        given(file.getOriginalFilename()).willReturn(originFileName);
        given(file.getSize()).willReturn(size);
        given(file.getContentType()).willReturn(contentType);

        // when
        var response = fileService.fileUpload(file, userId);

        // then
        assertThat(response)
                .matches(metadata -> StringUtils.contains(metadata.uploadFileName(), originFileName))
                .matches(metadata -> metadata.fileSize() == size)
                .matches(metadata -> metadata.userId() == userId);

    }
}
