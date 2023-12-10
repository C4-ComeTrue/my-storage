package com.c4cometrue.mystorage.repository;

import org.springframework.stereotype.Component;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileMetaDataReader {

	private final FileMetaDataRepository repository;

	public FileMetaData get(long id, long userId) {
		return repository.findByIdAndUserId(id, userId).orElseThrow(
			() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
	}

	public FileMetaData getRootFolder(long userId) {
		return repository.findByUserIdAndParent(userId, null).orElseThrow(
			() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
	}

	public void validateDuplicateFile(String fileName, long userId, FileMetaData parent) {
		if (repository.existsByFileNameAndUserIdAndParent(fileName, userId, parent)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FILE);
		}
	}

	public void validateDuplicateFolder(String folderName, long userId, FileMetaData parent) {
		if (repository.existsByFileNameAndUserIdAndParent(folderName, userId, parent)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FOLDER);
		}
	}

}
