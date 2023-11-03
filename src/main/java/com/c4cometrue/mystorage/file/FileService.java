package com.c4cometrue.mystorage.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileReader fileReader;
	private final FileWriter fileWriter;

	@Value("${file.buffer}")
	private int bufferSize;

	public void uploadFile(MultipartFile file, Long userId, Long parentId, String basePath) {
		String originalFileName = file.getOriginalFilename();
		String storedFileName = FileMetadata.storedName();
		Path path = Paths.get(basePath, storedFileName);
		FileMetadata fileMetadata = FileMetadata.builder()
			.originalFileName(originalFileName)
			.storedFileName(storedFileName)
			.filePath(path.toString())
			.uploaderId(userId)
			.parentId(parentId)
			.build();

		fileWriter.persist(fileMetadata, userId, parentId);
		FileUtil.uploadFile(file, path, bufferSize);
	}

	public void downloadFile(Long fileId, String userPath, Long userId) {
		FileMetadata fileMetadata = fileReader.findBy(fileId, userId);
		Path originalPath = Paths.get(fileMetadata.getFilePath());
		Path userDesignatedPath = Paths.get(userPath).resolve(fileMetadata.getOriginalFileName()).normalize();

		FileUtil.download(originalPath, userDesignatedPath, bufferSize);
	}

	@Transactional
	public void deleteFile(Long fileId, Long userId) {
		FileMetadata fileMetadata = fileReader.findBy(fileId, userId);
		fileWriter.deleteBy(fileId);
		Path path = Paths.get(fileMetadata.getFilePath());

		FileUtil.delete(path);
	}

	public List<FileMetadata> findChildBy(Long parentId, Long userId) {
		return fileReader.findChildBy(parentId, userId);
	}
}
