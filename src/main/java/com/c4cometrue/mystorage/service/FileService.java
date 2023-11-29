package com.c4cometrue.mystorage.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileMetaDataReader;
import com.c4cometrue.mystorage.repository.FileMetaDataWriter;
import com.c4cometrue.mystorage.utils.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

	private final FileUtil fileUtil;
	private final PathService pathService;
	private final FileMetaDataReader fileMetaDataReader;
	private final FileMetaDataWriter fileMetaDataWriter;

	public FileUploadDto.Response fileUpload(MultipartFile file, long userId, long folderId) {  // root folder Id 전달
		if (Objects.isNull(file) || file.isEmpty()) {
			throw new BusinessException(ErrorCode.FILE_EMPTY);
		}

		String originName = file.getOriginalFilename();
		if (isDuplicateFile(originName, userId)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FILE, originName);
		}

		FileMetaData parentFolder = fileMetaDataReader.get(folderId, userId);
		String uploadFileName = pathService.createUniqueFileName(originName);
		String uploadFullPath = pathService.getFullFilePath(parentFolder.getFolderPath(), uploadFileName);
		FileMetaData fileMetaData = fileMetaDataWriter.saveFileMetaData(
			file, userId, uploadFileName, parentFolder.getFolderPath()
		);
		fileMetaData.addParentFolder(parentFolder);

		fileUtil.uploadFile(file, uploadFullPath);
		return new FileUploadDto.Response(fileMetaData);
	}

	@Transactional(readOnly = true)
	public FileDownloadDto.Response fileDownLoad(long userId, long fileId) {
		FileMetaData fileMetaData = fileMetaDataReader.get(fileId, userId);
		validateFileAccess(userId, fileMetaData.getUserId());

		String uploadFilePath = pathService.getFullFilePath(fileMetaData.getFolderPath(),
			fileMetaData.getUploadName());
		Resource file = fileUtil.downloadFile(uploadFilePath);

		try {
			return new FileDownloadDto.Response(
				new FileDownloadDto.Bytes(file.getContentAsByteArray()),
				fileMetaData.getType()
			);
		} catch (IOException ex) {
			throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED,
				String.format("failed copying byte arrays from file : %s", file.getFilename()));
		}
	}

	public void fileDelete(long userId, long fileId) {
		FileMetaData fileMetaData = fileMetaDataReader.get(fileId, userId);
		validateFileAccess(userId, fileMetaData.getUserId());
		fileUtil.deleteFile(fileMetaData.getUploadName());
		fileMetaDataWriter.delete(fileMetaData);
	}

	private boolean isDuplicateFile(String fileName, long userId) {
		return fileMetaDataReader.isDuplicateFile(fileName, userId);
	}

	private void validateFileAccess(long userId, long fileUserId) {
		if (userId != fileUserId) {
			throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
		}
	}
}
