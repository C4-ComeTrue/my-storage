package com.c4cometrue.mystorage.controller;


import static com.c4cometrue.mystorage.controller.TestParameter.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.c4cometrue.mystorage.dto.response.ApiResponse;
import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.exception.FileException;
import com.c4cometrue.mystorage.service.FileService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class FileControllerTest {

    @Mock
    private FileService fileService;
    private FileController fileController;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        fileController = new FileController(fileService);
    }

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFileSuccess() {
        // Service Mock 예상 결과 설정
        when(fileService.uploadFile(mockMultipartFile, username)).thenReturn(createFileRes);
        // 컨트롤러 테스트 결과
        CreateFileRes response = fileController.uploadFile(mockMultipartFile, username);

        // 테스트 결과 검증
        assertNotNull(response);
        verify(fileService, times(1)).uploadFile(mockMultipartFile, username);
    }

    @Test
    @DisplayName("파일 업로드 실패 - 사용자 이름 없음")
    void uploadFileFailNoUsername() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file")
                .file(mockMultipartFile))
            .andExpect(status().isBadRequest());

        verify(fileService, times(0)).uploadFile(any(), any());
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 없음")
    void uploadFileNoFailFile() throws Exception {
        MockMultipartHttpServletRequestBuilder request = MockMvcRequestBuilders
            .multipart("/file")
            .part(new MockPart("username", username.getBytes()));

        // 파일 업로드 실패
        mockMvc.perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestPartException));

        verify(fileService, times(0)).uploadFile(any(), any());
    }


    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFile() {
        ApiResponse response = fileController.deleteFile(fileMetaData.getFileStorageName(), username);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(fileService, times(1)).deleteFile(any(), any());
    }

    @Test
    @DisplayName("파일 삭제 실패 - 삭제할 파일 이름 없음")
    void deleteFileFailNoFileName() throws Exception {
        mockMvc.perform(delete("/file?username=" + username))
            .andExpect(status().isBadRequest());

        verify(fileService, times(0)).deleteFile(any(), any());
    }

    @Test
    @DisplayName("파일 삭제 실패 - 사용자 이름 없음")
    void deleteFileFailNoUserName() throws Exception {
        mockMvc.perform(delete("/file?filename=" + fileMetaData.getFileStorageName()))
            .andExpect(status().isBadRequest());

        verify(fileService, times(0)).deleteFile(any(), any());
    }

    @Test
    @DisplayName("파일 삭제 실패 - 사용자 이름 불일치")
    void deleteFileFailDifferentUser() {
        doThrow(new FileException(HttpStatus.FORBIDDEN)).when(fileService).deleteFile(fileMetaData.getFileStorageName(), "other");
        // 파일 삭제 시도 및 권한 없음
        FileException e = assertThrows(FileException.class,
            () -> fileController.deleteFile(fileMetaData.getFileStorageName(), "other"));
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
        verify(fileService, times(1)).deleteFile(any(), any());
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadFile() {
        when(fileService.downloadFile(fileMetaData.getFileStorageName(), username)).thenReturn(fileDownloadRes);
        assertEquals(fileDownloadRes, fileController.downloadFile(fileMetaData.getFileStorageName(), username).getBody());
        verify(fileService, times(1)).downloadFile(any(), any());
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 파일 이름 없음")
    void downloadFileFailNoFileName() throws Exception {
        mockMvc.perform(get("/file")
                .param("username", username))
            .andExpect(status().isBadRequest());

        verify(fileService, times(0)).downloadFile(any(), any());
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 사용자 이름 없음")
    void downloadFileFailNoUserName() throws Exception {
        mockMvc.perform(get("/file")
                .param("filename", fileMetaData.getFileStorageName()))
            .andExpect(status().isBadRequest());

        verify(fileService, times(0)).downloadFile(any(), any());
    }



    @Test
    @DisplayName("파일 다운로드 실패 - 사용자 이름 불일치")
    void downloadFileFailDifferentUser() {
        doThrow(new FileException(HttpStatus.FORBIDDEN)).when(fileService).downloadFile(fileMetaData.getFileStorageName(), "other");
        // 파일 삭제 시도 및 권한 없음
        FileException e = assertThrows(FileException.class,
            () -> fileController.downloadFile(fileMetaData.getFileStorageName(), "other"));
        assertEquals(HttpStatus.FORBIDDEN, e.getHttpStatus());
        verify(fileService, times(1)).downloadFile(any(), any());
    }
}
