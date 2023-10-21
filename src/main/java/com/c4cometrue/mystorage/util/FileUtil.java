package com.c4cometrue.mystorage.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

public class FileUtil {
	private FileUtil() {
		throw new AssertionError("should not be invoke");
	}

	public static void uploadFile(MultipartFile file, Path path, int bufferSize) {
		try (InputStream is = file.getInputStream(); OutputStream os = Files.newOutputStream(path)) {
			byte[] buffer = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			throw new ServiceException(ErrorCode.FILE_COPY_ERROR);
		}
	}

	public static void download(Path originalPath, Path userDesignatedPath, int bufferSize) {
		try (InputStream is = Files.newInputStream(originalPath); OutputStream os = Files.newOutputStream(
			userDesignatedPath)) {
			byte[] buffer = new byte[bufferSize];
			int byteRead;
			while ((byteRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);
			}
		} catch (IOException e) {
			throw new ServiceException(ErrorCode.FILE_COPY_ERROR);
		}
	}

	public static void delete(Path path) {
		try {
			Files.delete(path.toAbsolutePath());
		} catch (IOException e) {
			throw new ServiceException(ErrorCode.FILE_DELETE_ERROR);
		}
	}
}
