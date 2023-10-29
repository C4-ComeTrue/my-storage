package com.c4cometrue.mystorage.file.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.exception.ErrorCode;

public class FileUtil {

	private FileUtil() {
	}

	public static void fileUpload(MultipartFile multipartFile, String savedPath) {
		if (multipartFile.isEmpty()) {
			throw ErrorCode.FILE_BAD_REQUEST.serviceException(); // 파일이 비어있을 경우
		}

		try {
			multipartFile.transferTo(new File(savedPath));
		} catch (IOException e) {
			throw ErrorCode.FILE_SERVER_ERROR.serviceException();
		}
	}

	public static void fileDownload(Path savedPath, Path downloadPath, int readCnt, byte[] buffer) {
		try (
			InputStream is = Files.newInputStream(savedPath);
			OutputStream os = Files.newOutputStream(downloadPath)
		) {
			while ((is.read(buffer)) != -1) {
				os.write(buffer, 0, readCnt);
			}
		} catch (IOException e) {
			throw ErrorCode.FILE_SERVER_ERROR.serviceException();
		}
	}

	public static void fileDelete(Path filePath) {
		if (!Files.exists(filePath)) {
			throw ErrorCode.FILE_NOT_EXIST.serviceException();
		}

		try {
			Files.delete(filePath);
		} catch (IOException e) {
			throw ErrorCode.FILE_SERVER_ERROR.serviceException();
		}
	}
}
