package com.c4cometrue.mystorage.file.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.file.entity.FileMetaData;
import com.c4cometrue.mystorage.file.repository.FileRepository;
import com.c4cometrue.mystorage.file.util.FileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

	private final FileRepository fileRepository;
	private final FilePathService filePathService;

	/**
	 * 업로드요청 파일을 정해진 path에 저장
	 * 파일에 대한 메타데이터를 db에 저장
	 * @param multipartFile : 업로드 요청 파일
	 * @param userName : 업로드 요청한 사용자 이름
	 */
	@Transactional
	public void fileUpload(MultipartFile multipartFile, String userName) {
		String originFileName = multipartFile.getOriginalFilename(); // 파일 원본이름
		checkFileDuplicated(originFileName, userName);

		Long fileSize = multipartFile.getSize();
		String fileMine = multipartFile.getContentType();
		String savedPath = filePathService.createSavedPath(originFileName);

		FileUtil.fileUpload(multipartFile, savedPath);
		FileMetaData fileMetaData = FileMetaData.builder()
			.fileName(originFileName)
			.userName(userName)
			.fileSize(fileSize)
			.savedPath(savedPath)
			.fileMine(fileMine)
			.build();

		fileRepository.save(fileMetaData);
	}

	public void checkFileDuplicated(String fileName, String userName) {
		if (!(fileRepository.findByFileNameAndUserName(fileName, userName)).isEmpty()) {
			throw ErrorCode.FILE_IS_DUPLICATED.serviceException();
		}
	}

	/**
	 * 파일을 사용자가 원하는 위치에 저장
	 * @param fileName 파일 원본이름
	 * @param userName 사용자 이름
	 * @param downPath 사용자가 다운받으려는 경로
	 */
	public void fileDownload(String fileName, String userName, String downPath) {

		FileMetaData savedFileMetaData = getFileMetaDataEntity(fileName, userName);

		Path savedPath = Paths.get(savedFileMetaData.getSavedPath());
		Path downloadPath = Paths.get(downPath).resolve(fileName).normalize();

		int readCnt = 0;
		int bufferSize = savedFileMetaData.getFileSize().intValue();
		if (bufferSize > 1024) {
			bufferSize = 1024;
		}
		byte[] buffer = new byte[bufferSize];

		FileUtil.fileDownload(savedPath, downloadPath, readCnt, buffer);
	}

	/**
	 * 원본파일, 파일
	 * @param fileName : 삭제하고자 하는 파일 이름
	 * @param userName : 삭제할 파일의 사용자 이름
	 */
	@Transactional
	public void fileDelete(String fileName, String userName) {

		FileMetaData savedFileMetaData = getFileMetaDataEntity(fileName, userName);

		Path path = Paths.get(savedFileMetaData.getSavedPath()); // 실제파일경로
		FileUtil.fileDelete(path.toAbsolutePath());
		fileRepository.delete(savedFileMetaData);
	}

	public FileMetaData getFileMetaDataEntity(String fileName, String userName) {

		// FileMetaDataEntity 찾아올 때, 두 가지 에러가 발생할 수 있다고 생각해서 분리
		FileMetaData savedFileMetaData = fileRepository.findByFileName(fileName)
			.orElseThrow(ErrorCode.FILE_NOT_EXIST::serviceException);

		if (savedFileMetaData.getUserName().equals(userName)) {
			return savedFileMetaData;
		} else {
			throw ErrorCode.FILE_PERMISSION_DENIED.serviceException();
		}
	}

}
