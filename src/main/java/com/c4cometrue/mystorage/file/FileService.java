package com.c4cometrue.mystorage.file;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileDataAccessService fileDataAccessService;

	@Value("${file.storage-path}")
	private String storagePath;

	@Value("${file.buffer}")
	private int bufferSize;

	@Transactional
	public void uploadFile(MultipartFile file, Long userId) {
		String originalFileName = file.getOriginalFilename();
		String storedFileName = Metadata.storedName();
		Path path = Paths.get(storagePath, storedFileName);
		Metadata metadata = Metadata.of(originalFileName, storedFileName, path.toString(), userId);

		fileDataAccessService.persist(metadata, userId);
		FileUtil.uploadFile(file, path, bufferSize);
	}

	public void downloadFile(Long fileId, String userPath, Long userId) {
		Metadata metadata = fileDataAccessService.findBy(fileId, userId);
		Path originalPath = Paths.get(metadata.getFilePath());
		Path userDesignatedPath = Paths.get(userPath).resolve(metadata.getOriginalFileName()).normalize();

		FileUtil.download(originalPath, userDesignatedPath, bufferSize);
	}

	@Transactional
	public void deleteFile(Long fileId, Long userId) {
		Metadata metadata = fileDataAccessService.findBy(fileId, userId);
		fileDataAccessService.deleteBy(fileId);
		Path path = Paths.get(metadata.getFilePath());

		FileUtil.delete(path);
	}
}
