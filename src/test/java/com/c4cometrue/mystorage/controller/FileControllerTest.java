package com.c4cometrue.mystorage.controller;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.FileException;
import com.c4cometrue.mystorage.repository.FileRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class FileControllerTest {
    @Autowired
    FileController fileController;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("파일 업로드 성공")
    void uploadFileSuccess() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        CreateFileRes createFileRes = fileController.uploadFile(multipartFile, username).getBody();
        assertNotNull(createFileRes);
        FileMetaData uploadedFile = fileRepository.findByFileStorageName(createFileRes.getFileStorageName());

        // 일치 검증
        assertEquals(uploadedFile.getOwner(), username, "사용자가 다릅니다.");
        assertEquals(uploadedFile.getFileName(), multipartFile.getOriginalFilename(), "파일 원본 이름이 다릅니다.");
        assertEquals(uploadedFile.getMime(), multipartFile.getContentType(), "타입이 다릅니다.");
    }

    @Test
    @DisplayName("파일 업로드 실패 - 사용자 이름 없음")
    void uploadFileFailUsername() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // 파일 업로드 실패
        assertThrows(ConstraintViolationException.class,
                () -> fileController.uploadFile(multipartFile, null));
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 없음")
    void uploadFileFailNoFile() throws Exception {
        // 요청 payload
        MockMultipartHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/file")
                .part(new MockPart("username", "sampleUsername".getBytes()));

        // 파일 업로드 실패
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestPartException));
    }

    @Test
    @DisplayName("파일 업로드 실패 - 파일 없음(Null)")
    void uploadFileFailFileNull() {
        // 요청 payload
        String username = "haeun";

        // 파일 업로드 실패
        assertThrows(ConstraintViolationException.class,
                () -> fileController.uploadFile(null, username));
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFile() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        CreateFileRes createFileRes = fileController.uploadFile(multipartFile, username).getBody();
        assertNotNull(createFileRes);
        FileMetaData uploadedFile = fileRepository.findByFileStorageName(createFileRes.getFileStorageName());

        ResponseEntity<String> response = fileController.deleteFile(uploadedFile.getFileStorageName(), username);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 실제로 파일이 저장소에서 삭제되었는지 검증
        FileMetaData maybeDeletedFile = fileRepository.findByFileStorageName(uploadedFile.getFileStorageName());
        assertThat(maybeDeletedFile).isNull();
    }

    @Test
    @DisplayName("파일 삭제 실패 - 삭제할 파일 이름 없음")
    void deleteFileFailNoFileName() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 파일 생성
        fileController.uploadFile(multipartFile, username);

        // 파일 삭제
        assertThrows(ConstraintViolationException.class,
                () -> fileController.deleteFile("", ""));
    }

    @Test
    @DisplayName("파일 삭제 실패 - 사용자 이름 없음")
    void deleteFileFailNoUserName() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 파일 생성
        ResponseEntity<CreateFileRes> response = fileController.uploadFile(multipartFile, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateFileRes createFileRes = response.getBody();
        assertNotNull(createFileRes);
        String fileName = createFileRes.getFileStorageName();

        // 파일 삭제
        assertThrows(ConstraintViolationException.class,
                () -> fileController.deleteFile(fileName, null));
    }

    @Test
    @DisplayName("파일 삭제 실패 - 사용자 이름 불일치")
    void deleteFileFailDifferentUser() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        ResponseEntity<CreateFileRes> response = fileController.uploadFile(multipartFile, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateFileRes createFileRes = response.getBody();
        assertNotNull(createFileRes);
        String fileName = createFileRes.getFileStorageName();

        // 파일 삭제 시도 및 권한 없음
        assertThrows(FileException.class,
                () -> fileController.deleteFile(fileName, "other"));
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadFile() throws IOException {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        ResponseEntity<CreateFileRes> response = fileController.uploadFile(multipartFile, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateFileRes createFileRes = response.getBody();
        assertNotNull(createFileRes);

        FileMetaData uploadedFile = fileRepository.findByFileStorageName(createFileRes.getFileStorageName());

        // 다운로드 받은 파일
        Resource downloadFile = fileController.downloadFile(uploadedFile.getFileStorageName(), username).getBody();
        assertNotNull(downloadFile);
        byte[] downloadFileBytes = StreamUtils.copyToByteArray(downloadFile.getInputStream());

        // 다운로드 검증
        assertEquals(uploadedFile.getOwner(), username, "소유자가 다릅니다.");
        assertEquals(uploadedFile.getFileName(), multipartFile.getOriginalFilename(), "파일 원본 이름이 다릅니다.");
        assertArrayEquals(downloadFileBytes, multipartFile.getBytes(), "파일이 다릅니다.");
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 파일 이름 없음")
    void downloadFileFailNoFileName() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",                  // form parameter name
                "test.txt",              // original file name
                "text/plain",            // content type
                "Hello, World!".getBytes() // file bytes
        );
        String username = "haeun";

        // 파일 생성
        fileController.uploadFile(multipartFile, username);

        // 다운로드 실패
        assertThrows(ConstraintViolationException.class,
                () -> fileController.downloadFile(null, "haeun"));
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 사용자 이름 없음")
    void downloadFileFailNoUserName() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        ResponseEntity<CreateFileRes> response = fileController.uploadFile(multipartFile, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateFileRes createFileRes = response.getBody();
        assertNotNull(createFileRes);
        String fileName = createFileRes.getFileStorageName();

        // 다운로드 실패
        assertThrows(ConstraintViolationException.class,
                () -> fileController.downloadFile(fileName, null));
    }



    @Test
    @DisplayName("파일 다운로드 실패 - 사용자 이름 불일치")
    void downloadFileFailDifferentUser() {
        // 요청 payload
        MultipartFile multipartFile = new MockMultipartFile(
                "uploadFile",
                "test.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );
        String username = "haeun";

        // 생성된 파일 정보
        ResponseEntity<CreateFileRes> response = fileController.uploadFile(multipartFile, username);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CreateFileRes createFileRes = response.getBody();
        assertNotNull(createFileRes);
        String fileName = createFileRes.getFileStorageName();

        // 파일 다운로드 시도 및 권한 없음
        assertThrows(FileException.class,
                () -> fileController.downloadFile(fileName, "other"));
    }
}