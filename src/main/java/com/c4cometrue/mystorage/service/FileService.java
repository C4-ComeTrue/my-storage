package com.c4cometrue.mystorage.service;

import java.io.IOException;

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

	public FileUploadDto.Response fileUpload(MultipartFile file, long userId, long folderId) {
		if (file.isEmpty()) {
			throw new BusinessException(ErrorCode.FILE_EMPTY);
		}

		FileMetaData rootFolder = fileMetaDataReader.getRootFolder(userId);
		FileMetaData parentFolder = getParentFolder(rootFolder, userId, folderId);

		// 이미 같은 폴더 아래에 파일이 존재하는지 확인
		String originName = file.getOriginalFilename();
		validateDuplicateFile(originName, userId, parentFolder);

		// 파일 메타데이터 저장
		String uploadFileName = pathService.createUniqueFileName(originName);
		FileMetaData fileMetaData = fileMetaDataWriter.saveFileMetaData(
			file, userId, uploadFileName, parentFolder
		);

		// 실제 물리 파일 저장
		String uploadFullPath = pathService.getFullFilePath(rootFolder.getFileName(), uploadFileName);
		fileUtil.uploadFile(file, uploadFullPath);
		return new FileUploadDto.Response(fileMetaData);
	}

	@Transactional(readOnly = true)
	public FileDownloadDto.Response fileDownLoad(long userId, long fileId) {
		// 파일 유효성 검증
		FileMetaData fileMetaData = fileMetaDataReader.get(fileId, userId);
		validateFileAccess(userId, fileMetaData.getUserId());

		// 루트 파일 아래에 저장되어 있는 파일 다운로드
		FileMetaData root = fileMetaDataReader.getRootFolder(userId);
		String uploadFilePath = pathService.getFullFilePath(root.getFileName(), fileMetaData.getUploadName());
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

	private FileMetaData getParentFolder(FileMetaData root, long userId, long folderId) {
		if (root.getId() != folderId) {
			fileMetaDataReader.get(folderId, userId);
		}
		return root;
	}

	private void validateFileAccess(long userId, long fileUserId) {
		if (userId != fileUserId) {
			throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
		}
	}

	public void validateDuplicateFile(String fileName, long userId, FileMetaData parent) {
		if (fileMetaDataReader.isDuplicateFile(fileName, userId, parent)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FILE);
		}
	}
}
