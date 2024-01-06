package com.c4cometrue.mystorage.file;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.exception.ErrorCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDataHandlerService {
	private final FileRepository fileRepository;
	@Transactional
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

	@Transactional
	public void persist(FileMetadata fileMetadata) {
		fileRepository.save(fileMetadata);
	}

	public void duplicateBy(Long parentId, Long userId, String fileName) {
		if (fileRepository.checkDuplicateFileName(parentId, userId, fileName)) {
			throw ErrorCode.DUPLICATE_FILE_NAME.serviceException("fileName : {}", fileName);
		}
	}

	public List<FileMetadata> getFileList(Long parentId, Long cursorId, Long userId, Pageable page) {
		return cursorId == null ? fileRepository.findAllByParentIdAndUploaderIdOrderByIdDesc(parentId, userId, page)
			: fileRepository.findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(parentId, cursorId, userId, page);
	}

	public Boolean hashNext(Long parentId, Long userId, Long lastIdOfList) {
		return fileRepository.existsByParentIdAndUploaderIdAndIdLessThan(parentId, userId, lastIdOfList);
	}

	public List<FileMetadata> findAllBy(Long parentId) {
		return fileRepository.findAllByParentId(parentId);
	}
}
