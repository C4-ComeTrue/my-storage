package com.c4cometrue.mystorage.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;
import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileDataAccessService fileDataAccessService;

	@Value("${file.storage-path}")
	private String storagePath;

	@Value("${file.buffer}")
	private Integer bufferSize;

	public void uploadFile(FileUploadRequest request) {
		MultipartFile file = request.multipartFile();
		Long userId = request.userId();

		String storedFileName = Metadata.storedName(file.getOriginalFilename() + UUID.randomUUID());
		Path path = Paths.get(storagePath, storedFileName);

		try (InputStream is = file.getInputStream(); OutputStream os = Files.newOutputStream(path)) {
			byte[] buffer = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			throw new ServiceException(ErrorCode.FILE_COPY_ERROR);
		}

		Metadata metadata = Metadata.of(file.getOriginalFilename(), storedFileName, path.toString(), userId);
		fileDataAccessService.persist(metadata);
	}

	public void downloadFile(FileDownloadRequest request) {
		Long fileId = request.fileId();
		Long userId = request.userId();
		String userPath = request.userPath();

		Metadata metadata = fileDataAccessService.findBy(fileId);
		metadata.validate(userId);
		Path originalPath = Paths.get(metadata.getFilePath());
		Path userDesignatedPath = Paths.get(userPath).resolve(metadata.getOriginalFileName()).normalize();

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

	public void deleteFile(FileDeleteRequest request) {
		Long fileId = request.fileId();
		Long userId = request.userId();

		Metadata metadata = fileDataAccessService.findBy(fileId);
		metadata.validate(userId);
		Path path = Paths.get(metadata.getFilePath());
		try {
			Files.delete(path.toAbsolutePath());
		} catch (IOException e) {
			throw new ServiceException(ErrorCode.FILE_DELETE_ERROR);
		}
		fileDataAccessService.deleteBy(fileId);
	}
}
