package com.c4cometrue.mystorage.file;

import java.util.List;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDataAccessService implements FileReader, FileWriter {
	private final FileRepository fileRepository;

	public void deleteBy(Long fileId) {
		existBy(fileId);
		fileRepository.deleteById(fileId);
	}

	private void existBy(Long fileId) {
		if (!fileRepository.existsById(fileId)) {
			throw ErrorCode.CANNOT_FOUND_FILE.serviceException("fileId : {}", fileId);
		}
	}

	public FileMetadata findBy(Long fileId, Long userId) {
		return fileRepository.findByIdAndUploaderId(fileId, userId)
			.orElseThrow(() -> ErrorCode.CANNOT_FOUND_FILE.serviceException("fileId : {}, userId : {}", fileId,
				userId));
	}

	public void persist(FileMetadata fileMetadata, Long userId, Long parentId) {
		validateFileOwnershipBy(parentId, userId);
		duplicateBy(fileMetadata.getOriginalFileName(), userId, parentId);
		fileRepository.save(fileMetadata);
	}

	private void duplicateBy(String fileName, Long userId, Long parentId) {
		if (fileRepository.checkDuplicateFileName(parentId, userId, fileName)) {
			throw ErrorCode.DUPLICATE_FILE_NAME.serviceException("fileName : {}", fileName);
		}
	}

	public List<FileMetadata> findChildBy(Long parentId, Long userId) {
		validateFileOwnershipBy(parentId, userId);
		return fileRepository.findByParentIdAndUploaderId(parentId, userId);
	}

	private void validateFileOwnershipBy(Long folderId, Long userId) {
		if (folderId != null && !fileRepository.existsByIdAndUploaderId(folderId, userId)) {
			throw ErrorCode.UNAUTHORIZED_FILE_ACCESS.serviceException();
		}
	}
}
