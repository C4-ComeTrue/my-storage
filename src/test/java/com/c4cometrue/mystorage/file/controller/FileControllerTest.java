package com.c4cometrue.mystorage.file.controller;

import com.c4cometrue.mystorage.file.controller.FileController;
import com.c4cometrue.mystorage.file.dto.FileDeleteRequestDto;
import com.c4cometrue.mystorage.file.dto.FileDownloadRequestDto;
import com.c4cometrue.mystorage.file.service.FileService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.c4cometrue.mystorage.TestMockFile.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {
	// controller에서 하는 역할
	// method요청받고, 그에 맞게 service레이어로 넘기기
	// 실제 파일에 메소드 수행 성공, 실패는 service에서 이뤄지므로, 여기서는 실패케이스를 고려하지 않아도됨.
	// 검증은 어떻게 할 것인지에 대해 생각해보기

	@InjectMocks
	private FileController fileController;

	@Mock
	private FileService fileService;

	@Test
	@DisplayName("파일 업로드 테스트")
	void fileUpload() {
		fileController.fileUpload(mockMultipartFile, mockUserName);

		verify(fileService, times(1))
			.fileUpload(mockMultipartFile, mockUserName);
	}

	@Test
	@DisplayName("파일 다운로드 테스트")
	void fileDownload() {
		fileController.fileDownload(FileDownloadRequestDto.create(mockFileName, mockUserName, mockFilePath));

		verify(fileService, times(1))
			.fileDownload(mockFileName, mockUserName, mockFilePath);
	}

	@Test
	@DisplayName("파일 삭제 테스트")
	void fileDelete() {
		fileController.fileDelete(FileDeleteRequestDto.create(mockFileName, mockUserName));

		verify(fileService, times(1)).fileDelete(mockFileName, mockUserName);
	}
}
