package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileMetaDataRepository;
import com.c4cometrue.mystorage.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    FileMetaDataRepository fileMetaDataRepository;

    @Mock
    FileUtil fileUtil;

    @InjectMocks
    FileService fileService;

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
        given(fileMetaDataRepository.existsByFileNameAndUserId(anyString(), anyLong()))
                .willReturn(true);

        // when + then
        assertThatThrownBy(() -> {
            fileService.fileUpload(file, userId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_FILE.getMessage());
    }

    @Test
    void 파일_메타데이터를_등록한다() {
        // given
        var fileId = 1L;
        var userId = 1L;
        var file = mock(MultipartFile.class);
        var originFileName = "dd.jpg";
        var size = 1000L;
        var contentType = "text/plain";
        var fileMetaData = mock(FileMetaData.class);

        given(file.getOriginalFilename()).willReturn(originFileName);
        given(file.getSize()).willReturn(size);
        given(file.getContentType()).willReturn(contentType);
        given(fileMetaData.getId()).willReturn(fileId);
        given(fileMetaData.getUserId()).willReturn(userId);
        given(fileMetaData.getSize()).willReturn(size);
        given(fileMetaData.getUploadName()).willReturn(originFileName);

        given(fileMetaDataRepository.save(any())).willReturn(fileMetaData);

        // when
        var response = fileService.fileUpload(file, userId);

        // then
        assertThat(response)
                .matches(metadata -> StringUtils.contains(metadata.uploadFileName(), originFileName))
                .matches(metadata -> metadata.fileSize() == size)
                .matches(metadata -> metadata.userId() == userId);
    }

    @Test
    void 저장된_파일이_아니라면_다운로드에_실패한다() {
        var userId = 1L;
        var fileId = 1L;

        assertThatThrownBy(() -> {
            fileService.fileDownLoad(userId, fileId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FILE_NOT_FOUND.getMessage());
    }

    @Test
    void 본인이_업로드한_파일이_아니라면_다운로드에_실패한다() {
        // given
        var userId = 2L;
        var fileId = 1L;
        var fileMetaData = FileMetaData.builder()
                .userId(1L)
                .fileName("name.jpg")
                .uploadName("name.jp")
                .size(1000L)
                .type(MediaType.IMAGE_JPEG.getType())
                .build();

        given(fileMetaDataRepository.findById(anyLong())).willReturn(Optional.of(fileMetaData));

        // when + then
        assertThatThrownBy(() -> {
            fileService.fileDownLoad(userId, fileId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_FILE_ACCESS.getMessage());
    }

    @Test
    void 파일을_다운로드에_성공한다() throws IOException {
        // given
        var userId = 1L;
        var fileId = 1L;
        var contentType = MediaType.IMAGE_JPEG.getType();
        var fileName = "name.jpg";
        var fileMetaData = FileMetaData.builder()
                .userId(userId)
                .fileName(fileName)
                .uploadName(fileName)
                .size(1000L)
                .type(contentType)
                .build();

        var byteArray = new byte[1];
        var resource = mock(UrlResource.class);
        lenient().when(resource.getFilename()).thenReturn(fileName);
        lenient().when(resource.getContentAsByteArray()).thenReturn(byteArray);

        given(fileMetaDataRepository.findById(anyLong())).willReturn(Optional.of(fileMetaData));
        given(fileUtil.downloadFile(anyString())).willReturn(resource);

        // when
        var response = fileService.fileDownLoad(userId, fileId);

        // then
        assertThat(response)
                .matches(res -> res.contentType().equals(contentType))
                .matches(res -> Arrays.equals(res.data(), byteArray));
    }


    @Test
    void 저장된_파일이_아니라면_삭제에_실패한다() {
        var userId = 1L;
        var fileId = 1L;

        assertThatThrownBy(() -> {
            fileService.fileDelete(userId, fileId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.FILE_NOT_FOUND.getMessage());
    }

    @Test
    void 본인이_업로드한_파일이_아니라면_삭제에_실패한다() {
        // given
        var userId = 2L;
        var fileId = 1L;
        var fileMetaData = FileMetaData.builder()
                .userId(1L)
                .fileName("name.jpg")
                .uploadName("name.jp")
                .size(1000L)
                .type(MediaType.IMAGE_JPEG.getType())
                .build();

        given(fileMetaDataRepository.findById(anyLong())).willReturn(Optional.of(fileMetaData));

        // when + then
        assertThatThrownBy(() -> {
            fileService.fileDelete(userId, fileId);
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.INVALID_FILE_ACCESS.getMessage());
    }

    @Test
    void 파일_삭제에_성공한다() {
        // given
        var userId = 1L;
        var fileId = 1L;
        var fileMetaData = FileMetaData.builder()
                .userId(userId)
                .fileName("name.jpg")
                .uploadName("name.jp")
                .size(1000L)
                .type(MediaType.IMAGE_JPEG.getType())
                .build();

        given(fileMetaDataRepository.findById(anyLong())).willReturn(Optional.of(fileMetaData));

        // when
        fileService.fileDelete(userId, fileId);

        // then
        verify(fileUtil, times(1)).deleteFile(anyString());
    }
}
