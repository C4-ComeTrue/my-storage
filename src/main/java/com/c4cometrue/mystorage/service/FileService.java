package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.request.FileReq;
import com.c4cometrue.mystorage.dto.request.UploadFileReq;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.dto.response.FileMetaDataRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final FileRepository fileRepository;
	private final ResourceLoader resourceLoader;
	private final FolderRepository folderRepository;

	/**
	 * 파일 업로드
	 * @param uploadFileReq 파일(MultiPartFile)과 사용자 이름, 폴더 아이디 포함
	 * @return 파일의 메타 데이터
	 */
	@Transactional
	public FileMetaDataRes uploadFile(UploadFileReq uploadFileReq) {
		// 폴더 존재 확인
		Path folderPath = getFolderPath(uploadFileReq.folderId());
		MultipartFile file = uploadFileReq.file();
		// 특정 사용자의 동일한 파일명 중복 처리
		if (fileRepository.findByFileNameAndUserNameAndFolderId(
			file.getOriginalFilename(), uploadFileReq.userName(), uploadFileReq.folderId()).isPresent()) {
			throw ErrorCd.DUPLICATE_FILE.serviceException();
		}
		String fileStorageName = UUID.randomUUID() + file.getOriginalFilename();

		// 물리적 저장
		Path filePath = folderPath.resolve(fileStorageName);
		FileUtil.uploadFile(file, filePath);


		FileMetaData fileMetaData = FileMetaData.builder()
			.fileName(file.getOriginalFilename())
			.fileStorageName(fileStorageName)
			.size(file.getSize())
			.mime(file.getContentType())
			.userName(uploadFileReq.userName())
			.folderId(uploadFileReq.folderId())
			.build();
		// DB 저장
		fileRepository.save(fileMetaData);
		return new FileMetaDataRes(fileMetaData);
	}

	/**
	 * 파일 삭제
	 * @param fileReq 파일 저장소 이름, 사용자 이름, 폴더 아이디
	 */
	@Transactional
	public void deleteFile(FileReq fileReq) {
		// 폴더 존재 확인
		Path folderPath = getFolderPath(fileReq.folderId());
		// 파일 데이터 조회
		FileMetaData fileMetaData = getFileMetaData(fileReq.fileStorageName(), fileReq.userName());
		// 파일 물리적 경로
		Path filePath = folderPath.resolve(fileMetaData.getFileStorageName());
		// 파일 DB 정보 삭제
		fileRepository.delete(fileMetaData);
		// 파일 물리적 삭제
		FileUtil.deleteFile(filePath);
	}

	/**
	 * 파일 다운로드
	 * @param fileReq 파일 저장소 이름 / 사용자 이름 / 폴더 아이디
	 * @return 파일 Resource 데이터 및 메타 데이터
	 */
	public FileDownloadRes downloadFile(FileReq fileReq) {
		// 폴더 존재 확인
		Path folderPath = getFolderPath(fileReq.folderId());
		// 파일 메타 데이터 조회
		FileMetaData fileMetaData = getFileMetaData(fileReq.fileStorageName(), fileReq.userName());
		// 파일 물리적 경로
		Path filePath = folderPath.resolve(fileMetaData.getFileStorageName());
		// 파일 데이터 로드
		Resource file = resourceLoader.getResource("file:" + filePath.toString());
		if (!file.exists()) {
			throw ErrorCd.FILE_NOT_EXIST.serviceException(
				"[downloadFile] file doesn't exist - fileStorageName {}", fileMetaData.getFileId());
		}
		return new FileDownloadRes(file, fileMetaData.getFileName(), fileMetaData.getMime());
	}

	/**
	 * 폴더의 Path 가져오기
	 * @param folderId 폴더 PK
	 * @return 폴더 Path
	 */
	private Path getFolderPath(long folderId) {
		String folderPath = folderRepository.findFolderPathByFolderId(folderId).orElseThrow(
			() -> ErrorCd.FOLDER_NOT_EXIST.serviceException("folder doesn't exist!")
		);
		return Path.of(folderPath);
	}

	/**
	 * 파일이 DB에 존재하는지 확인
	 * @param fileStorageName 파일 로컬 저장소 이름
	 * @param username 사용자 이름
	 * @return 파일 메타 데이터
	 */
	FileMetaData getFileMetaData(String fileStorageName, String username) {
		FileMetaData fileMetaData = fileRepository.findByFileStorageName(fileStorageName)
			.orElseThrow(() -> ErrorCd.FILE_NOT_EXIST
				.serviceException("[getFileMetaData] file not exist - fileStorageName: {}", fileStorageName));

		if (!fileMetaData.getUserName().equals(username)) {
			throw ErrorCd.NO_PERMISSION
				.serviceException("[getFileMetaData] no permission - userName: {}", username);
		}

		return fileMetaData;
	}
}
