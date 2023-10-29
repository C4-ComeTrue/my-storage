package com.c4cometrue.mystorage.util;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;
import com.c4cometrue.mystorage.file.util.FileUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.c4cometrue.mystorage.TestMockFile.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FileUtilTest {

	private static MockedStatic<Files> filesMockedStatic;

	@Test
	@DisplayName("파일 업로드 실패")
	void fileUploadFailEmptyTest() {
		given(mockMultipartFile.isEmpty()).willReturn(true);

		var emptyException = assertThrows(ServiceException.class,
			() -> FileUtil.fileUpload(mockMultipartFile, mockFilePath));

		assertEquals(ErrorCode.FILE_BAD_REQUEST.name(), emptyException.getErrorCode());
	}

	//    @Test
	//    @DisplayName("파일 업로드 도중 실패")
	//    void fileUploadFailIOTest() throws IOException{
	////        doThrow(new IOException()).when(mockMultipartFile).transferTo(new File(mockFilePath));
	//        filesMockedStatic.when(() -> mockMultipartFile.transferTo(new File(mockFilePath)))
	//                .thenThrow(IOException.class);
	//        assertThrows(IOException.class, () -> FileUtil.fileUpload(mockMultipartFile, mockUserName));
	//    }

	@Test
	@DisplayName("파일 삭제 실패: 파일이 없는 경우")
	void fileDeleteFailEmptyTest() {
		given(Files.exists(mockDeletePath)).willReturn(false);

		var exception = assertThrows(ServiceException.class, () -> FileUtil.fileDelete(mockDeletePath));
		assertEquals(ErrorCode.FILE_NOT_EXIST.name(), exception.getErrorCode());
	}

	// @Test
	// @DisplayName("파일 삭제 실패 : 삭제 진행 중 실패")
	// void fileDeleteFailTest() {
	//     var wrongPath = Path.of("wrong Path");
	//     given(Files.exists(wrongPath)).willReturn(true);
	//     filesMockedStatic.when(() -> Files.delete(wrongPath)).thenThrow(IOException.class);
	//     var exception = assertThrows(ServiceException.class, () -> FileUtil.fileDelete(wrongPath));
	//
	//     assertEquals(ErrorCode.FILE_SERVER_ERROR.name(), exception.getErrorCode());
	// }

}
