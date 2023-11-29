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
		MultipartFile file, long userId, String uploadFileName, String folderPath
	) {
		FileMetaData fileMetaData = FileMetaData.fileBuilder()
			.userId(userId)
			.fileName(file.getOriginalFilename())
			.uploadName(uploadFileName)
			.size(file.getSize())
			.type(file.getContentType())
			.folderPath(folderPath)
			.fileType(FileType.FILE)
			.build();

		return repository.save(fileMetaData);
	}

	public FileMetaData saveFolderMetaData(
		long userId, String folderName, String folderPath, FileMetaData parent
	) {
		FileMetaData folderMetaData = FileMetaData.folderBuilder()
			.userId(userId)
			.fileName(folderName)
			.folderPath(folderPath)
			.uploadName(".")
			.fileType(FileType.FOLDER)
			.build();

		folderMetaData.addParentFolder(parent);
		return repository.save(folderMetaData);
	}

	public void delete(FileMetaData fileMetaData) {
		repository.delete(fileMetaData);
	}
}
