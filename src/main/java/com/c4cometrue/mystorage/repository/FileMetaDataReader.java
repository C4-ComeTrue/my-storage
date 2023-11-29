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

	public boolean isDuplicateFile(String fileName, long userId) {
		return repository.existsByFileNameAndUserId(fileName, userId);
	}

	public boolean isDuplicateFolderName(FileMetaData folder, String name) {
		return repository.existsByParentAndFileName(folder, name);
	}
}
