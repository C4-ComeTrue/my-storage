package com.c4cometrue.mystorage.repository;

import java.util.List;

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
			() -> new BusinessException(ErrorCode.FOLDER_NOT_FOUND));
	}

	public boolean isDuplicateFile(String fileName, long userId, FileMetaData parent) {
		return repository.existsByFileNameAndUserIdAndParent(fileName, userId, parent);
	}

	public List<FileMetaData> getFiles(long userId, FileMetaData parent) {
		return repository.findAllByUserIdAndParent(userId, parent);
	}

}
