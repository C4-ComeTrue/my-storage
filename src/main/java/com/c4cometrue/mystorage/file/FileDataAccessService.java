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
		fileRepository.deleteById(fileId);
	}

	public Metadata findBy(Long fileId, Long userId) {
		return fileRepository.findByIdAndUploaderId(fileId, userId).orElseThrow(
			() -> new ServiceException(ErrorCode.UNAUTHORIZED_FILE_ACCESS)
		);
	}

	public void persist(Metadata metadata) {
		fileRepository.save(metadata);
	}
}
