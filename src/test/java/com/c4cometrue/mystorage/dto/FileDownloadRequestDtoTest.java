package com.c4cometrue.mystorage.dto;


import com.c4cometrue.mystorage.file.dto.FileDownloadRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.c4cometrue.mystorage.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FileDownloadRequestDtoTest {

    @Mock
    FileDownloadRequestDto fileDownloadRequestDto;

    @DisplayName("파일다운로드 요청dto 생성 테스트")
    @Test
    void creatTest() {
        // given
        FileDownloadRequestDto dto = fileDownloadRequestDto.create(mockFileName, mockUserName, mockDownRootPath);
        
        //then
        assertEquals(dto.fileName(), mockFileName);
        assertEquals(dto.userName(), mockUserName);
        assertEquals(dto.downloadPath(), mockDownRootPath);

    }


}
