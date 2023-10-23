package com.c4cometrue.mystorage.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileUtil {

	private static final String URL_PROTOCOL = "file:";
	private final ResourceLoader resourceLoader;

	public void uploadFile(MultipartFile file, String fileUploadPath) {
		Path destinationPath = Path.of(fileUploadPath);

		try {
			file.transferTo(destinationPath);
		} catch (IOException ex) {
			throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
		}
	}

	public Resource downloadFile(String fileUploadPath) {
		String destinationPath = URL_PROTOCOL + fileUploadPath;
		Resource resource = resourceLoader.getResource(destinationPath);

		if (!resource.exists() || !resource.isReadable()) {
			throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED);
		}

		return resource;
	}

	public void deleteFile(String fileUploadPath) {
		Path destinationPath = Path.of(fileUploadPath);

		if (!Files.exists(destinationPath)) {
			throw new BusinessException(ErrorCode.FILE_EMPTY);
		}

		try {
			Files.delete(destinationPath);
		} catch (IOException ex) {
			throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, ex);
		}
	}

}
