package com.c4cometrue.mystorage.file;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileDataAccessService implements FileReader, FileWriter {
	private final FileRepository fileRepository;

	public void deleteBy(Long fileId) {
		existBy(fileId);
		fileRepository.deleteById(fileId);
	}

	public Metadata findBy(Long fileId, Long userId) {
		return fileRepository.findByIdAndUploaderId(fileId, userId)
			.orElseThrow(() -> ErrorCode.UNAUTHORIZED_FILE_ACCESS.serviceException("fileId : {}, userId : {}", fileId,
				userId));
	}

	public void persist(Metadata metadata, Long userId) {
		duplicateBy(metadata.getOriginalFileName(), userId);
		fileRepository.save(metadata);
	}

	public void existBy(Long fileId) {
		if (!fileRepository.existsById(fileId)) {
			throw ErrorCode.CANNOT_FOUND_FILE.serviceException("fileId : {}", fileId);
		}
	}

	public void duplicateBy(String fileName, Long userId) {
		if (fileRepository.checkDuplicateFileName(fileName, userId)) {
			throw ErrorCode.DUPLICATE_FILE_NAME.serviceException("fileName : {}", fileName);
		}
	}
}
