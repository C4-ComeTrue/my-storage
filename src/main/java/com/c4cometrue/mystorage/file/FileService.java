package com.c4cometrue.mystorage.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.file.dto.CursorFileResponse;
import com.c4cometrue.mystorage.file.dto.FileContent;
import com.c4cometrue.mystorage.folder.FolderService;
import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileDataHandlerService fileDataHandlerService;
	private final FolderService folderService;


	@Value("${file.buffer}")
	private int bufferSize;

	public void uploadFile(MultipartFile file, Long userId, Long parentId) {
		String basePath = folderService.findPathBy();

		String originalFileName = file.getOriginalFilename();
		fileDataHandlerService.duplicateBy(parentId, userId, originalFileName);

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
		fileDataHandlerService.persist(fileMetadata);
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

	public CursorFileResponse getFiles(Long parentId, Long cursorId, Long userId, Pageable page) {
		List<FileMetadata> files = fileDataHandlerService.getFileList(parentId, cursorId, userId, page);
		List<FileContent> fileContents = files.stream()
			.map(file -> FileContent.of(file.getId(), file.getOriginalFileName()))
			.toList();
		Long lastIdOfList = files.isEmpty() ? null : files.get(files.size() - 1).getId();
		return CursorFileResponse.of(fileContents, fileDataHandlerService.hashNext(parentId, userId, lastIdOfList));
	}

	@Transactional
	public void moveFile(Long fileId, Long userId, Long destinationFolderId) {
		// 해당 폴더가 접근 가능 한 폴더 인지 체크
		folderService.validateBy(destinationFolderId, userId);

		FileMetadata fileMetadata = fileDataHandlerService.findBy(fileId, userId);

		// 부모 폴더(파일이 속한 폴더) 바꾸기
		fileMetadata.changeParentId(destinationFolderId);

		// 변경 사항을 저장
		fileDataHandlerService.persist(fileMetadata);
	}

	// 소프트 딜리트
	public void deleteFile(FileMetadata fileMetadata) {
		fileMetadata.deleteFile();
		fileDataHandlerService.persist(fileMetadata);
	}

	public List<FileMetadata> findAllBy(Long parentId) {
		return fileDataHandlerService.findAllBy(parentId);
	}
}
