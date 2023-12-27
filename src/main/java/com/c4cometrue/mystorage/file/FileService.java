package com.c4cometrue.mystorage.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.file.dto.FileContent;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileDataHandlerService fileDataHandlerService;
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
		fileDataHandlerService.persist(fileMetadata, userId, parentId);
	}

	public void downloadFile(Long fileId, String userPath, Long userId) {
		FileMetadata fileMetadata = fileDataHandlerService.findBy(fileId, userId);
		Path originalPath = Paths.get(fileMetadata.getFilePath());
		Path userDesignatedPath = Paths.get(userPath).resolve(fileMetadata.getOriginalFileName()).normalize();

		FileUtil.download(originalPath, userDesignatedPath, bufferSize);
	}

	public void deleteFile(Long fileId, Long userId) {
		FileMetadata fileMetadata = fileDataHandlerService.findBy(fileId, userId);
		Path path = Paths.get(fileMetadata.getFilePath());

		FileUtil.delete(path);

		fileDataHandlerService.deleteBy(fileId);
	}

	public List<FileMetadata> findChildBy(Long parentId, Long userId) {
		return fileDataHandlerService.findChildBy(parentId, userId);
	}

	public CursorFileResponse getFiles(Long parentId, Long cursorId, Long userId, Pageable page) {
		List<FileMetadata> files = fileDataHandlerService.getFileList(parentId, cursorId, userId, page);
		List<FileContent> fileContents = files.stream()
			.map(file -> FileContent.of(file.getId(), file.getOriginalFileName()))
			.collect(Collectors.toList());
		Long lastIdOfList = files.isEmpty() ? null : files.get(files.size() - 1).getId();
		return CursorFileResponse.of(fileContents, fileDataHandlerService.hashNext(parentId, userId, lastIdOfList));
	}
}
