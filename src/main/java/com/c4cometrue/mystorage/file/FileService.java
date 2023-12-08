package com.c4cometrue.mystorage.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileReader fileReader;
	private final FileWriter fileWriter;
	private final FolderService folderService;

	@Value("${file.buffer}")
	private int bufferSize;

	public void uploadFile(MultipartFile file, Long userId, Long parentId) {
		String basePath = folderService.findPathBy(parentId);
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

		FileUtil.uploadFile(file, path, bufferSize);
		fileWriter.persist(fileMetadata, userId, parentId);
	}

	public void downloadFile(Long fileId, String userPath, Long userId) {
		FileMetadata fileMetadata = fileReader.findBy(fileId, userId);
		Path originalPath = Paths.get(fileMetadata.getFilePath());
		Path userDesignatedPath = Paths.get(userPath).resolve(fileMetadata.getOriginalFileName()).normalize();

		FileUtil.download(originalPath, userDesignatedPath, bufferSize);
	}

	public void deleteFile(Long fileId, Long userId) {
		FileMetadata fileMetadata = fileReader.findBy(fileId, userId);
		Path path = Paths.get(fileMetadata.getFilePath());

		FileUtil.delete(path);

		fileWriter.deleteBy(fileId);
	}

	public List<FileMetadata> findChildBy(Long parentId, Long userId) {
		return fileReader.findChildBy(parentId, userId);
	}
}
