package com.c4cometrue.mystorage.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileMetaDataRepository;
import com.c4cometrue.mystorage.utils.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

	private static final String UPLOAD_DIR = "/Users/kangsemi/Desktop/git/files/";

	private final FileMetaDataRepository fileMetaDataRepository;

	private final FileUtil fileUtil;

	/**
	 * 파일 업로드
	 * @param file
	 * @param userId
	 * @return fileId, userId, uploadFilePath, fileSize
	 */
	@Transactional
	public FileUploadDto.Response fileUpload(MultipartFile file, long userId) {
		if (Objects.isNull(file) || file.isEmpty()) {
			throw new BusinessException(ErrorCode.FILE_EMPTY);
		}

		String originName = file.getOriginalFilename();
		if (isDuplicateFile(originName, userId)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FILE);
		}

		String uploadFilePath = getFullUploadFilePath(originName);
		FileMetaData fileMetaData = saveFileMetaData(file, userId, uploadFilePath);

		fileUtil.uploadFile(file, uploadFilePath);
		return new FileUploadDto.Response(fileMetaData);
	}

	private boolean isDuplicateFile(String fileName, long userId) {
		return fileMetaDataRepository.existsByFileNameAndUserId(fileName, userId);
	}

	private String getFullUploadFilePath(String originalFileName) {
		String uploadName = UUID.randomUUID() + originalFileName;
		String fullUploadPAth = UPLOAD_DIR + uploadName;
		validateFileUploadPath(fullUploadPAth);
		return fullUploadPAth;
	}

	private void validateFileUploadPath(String path) {
		try {
			File file = new File(path);
			String canonicalDestPath = file.getCanonicalPath();

			if (!canonicalDestPath.startsWith(UPLOAD_DIR)) {
				throw new BusinessException(ErrorCode.INVALID_FILE_PATH);
			}

			file.deleteOnExit();
		} catch (IOException ex) {
			throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
		}
	}

	private FileMetaData saveFileMetaData(MultipartFile file, long userId, String uploadFileName) {
		FileMetaData fileMetaData = FileMetaData.builder()
			.userId(userId)
			.fileName(file.getOriginalFilename())
			.uploadName(uploadFileName)
			.size(file.getSize())
			.type(file.getContentType())
			.build();

		return fileMetaDataRepository.save(fileMetaData);
	}

	/**
	 * 파일 다운로드
	 * @param userId
	 * @param fileId
	 * @return fileContentByteArray, FileContentType
	 */
	public FileDownloadDto.Response fileDownLoad(long userId, long fileId) {
		FileMetaData fileMetaData = getFileMetaData(fileId);

		if (hasFileAccess(userId, fileMetaData.getUserId())) {
			throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
		}

		Resource file = fileUtil.downloadFile(fileMetaData.getUploadName());
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

	/**
	 * 파일 삭제
	 * @param fileId
	 * @param userId
	 */
	@Transactional
	public void fileDelete(long userId, long fileId) {
		FileMetaData fileMetaData = getFileMetaData(fileId);

		if (hasFileAccess(userId, fileMetaData.getUserId())) {
			throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
		}

		fileUtil.deleteFile(fileMetaData.getUploadName());
	}

	private FileMetaData getFileMetaData(long fileId) {
		return fileMetaDataRepository.findById(fileId).orElseThrow(
			() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
	}

	private boolean hasFileAccess(long userId, long fileUserId) {
		return userId != fileUserId;
	}

}
