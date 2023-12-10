package com.c4cometrue.mystorage.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileMetaDataWriter {

	private final FileMetaDataRepository repository;

	public FileMetaData saveFileMetaData(
		MultipartFile file, long userId, String uploadFileName, FileMetaData parent
	) {
		FileMetaData fileMetaData = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(file.getOriginalFilename())
			.uploadName(uploadFileName)
			.size(file.getSize())
			.type(file.getContentType())
			.parent(parent)
			.fileType(FileType.FILE)
			.build();

		return repository.save(fileMetaData);
	}

	public FileMetaData saveFolderMetaData(
		long userId, String folderName, FileMetaData parent
	) {
		FileMetaData folderMetaData = FileMetaData.folderBuilder()
			.userId(userId)
			.fileName(folderName)
			.uploadName(".")
			.fileType(FileType.FOLDER)
			.parent(parent)
			.build();

		return repository.save(folderMetaData);
	}

	public void delete(FileMetaData fileMetaData) {
		repository.delete(fileMetaData);
	}
}
