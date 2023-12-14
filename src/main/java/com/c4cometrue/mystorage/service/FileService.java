package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.response.file.FileDownloadRes;
import com.c4cometrue.mystorage.dto.response.file.FileMetaDataRes;
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
	private final StoragePathService storagePathService;

	/**
	 * 파일 업로드
	 * @param file     업로드할 파일
	 * @param userName 사용자 이름
	 * @param folderId 파일이 업로드 될 폴더 기본키
	 * @return 파일의 메타 데이터
	 */
	@Transactional
	public FileMetaDataRes uploadFile(MultipartFile file, String userName, long folderId) {
		// 폴더 확인
		checkFolder(folderId);
		// 특정 사용자의 동일한 파일명 중복 처리
		checkDuplicate(folderId, userName, file.getOriginalFilename());
		// 파일 저장소 이름
		String fileStorageName = getFileName(file.getOriginalFilename());

		// DB 저장 -> 롤백 가능
		FileMetaData fileMetaData = FileMetaData.builder()
			.fileName(file.getOriginalFilename())
			.fileStorageName(fileStorageName)
			.size(file.getSize())
			.mime(file.getContentType())
			.userName(userName)
			.folderId(folderId)
			.build();
		fileRepository.save(fileMetaData);

		// 물리적 저장 -> 롤백 불가능
		Path filePath = getFilePath(userName, fileStorageName);
		FileUtil.uploadFile(file, filePath);

		return new FileMetaDataRes(fileMetaData);
	}

	/**
	 * 파일 삭제
	 * @param fileStorageName 파일 저장소 이름
	 * @param userName 사용자 이름
	 * @param folderId 파일이 존재하는 폴더 기본키
	 */
	@Transactional
	public void deleteFile(String fileStorageName, String userName, long folderId) {
		// 폴더 존재 확인
		checkFolder(folderId);
		// 파일 데이터 조회
		FileMetaData fileMetaData = getFileMetaData(fileStorageName, userName);
		// 파일 DB 정보 삭제
		fileRepository.delete(fileMetaData);
		// 파일 물리적 삭제
		Path filePath = getFilePath(userName, fileStorageName);
		FileUtil.deleteFile(filePath);
	}

	/**
	 * 파일 다운로드
	 * @param fileStorageName 파일 저장소 이름
	 * @param userName 사용자 이름
	 * @param folderId 삭제할 파일이 존재하는 폴더 기본키
	 * @return 파일 Resource 데이터 및 메타 데이터
	 */
	public FileDownloadRes downloadFile(String fileStorageName, String userName, long folderId) {
		// 폴더 존재 확인
		checkFolder(folderId);
		// 파일 메타 데이터 조회
		FileMetaData fileMetaData = getFileMetaData(fileStorageName, userName);
		// 파일 물리적 경로
		Path filePath = getFilePath(userName, fileStorageName);
		return new FileDownloadRes(getFileResource(filePath), fileMetaData.getFileName(), fileMetaData.getMime());
	}

	/**
	 * 폴더가 DB에 존재하는지 확인한다.
	 * @param folderId 폴더 기본키
	 */
	private void checkFolder(long folderId) {
		if (folderRepository.findByFolderId(folderId).isEmpty()) {
			throw ErrorCd.FOLDER_NOT_EXIST.serviceException();
		}
	}

	/**
	 * 파일이 중복인지 확인한다.
	 * @param folderId 파일이 존재할 폴더 기본키
	 * @param userName 사용자 이름
	 * @param fileName 파일 이름
	 */
	private void checkDuplicate(long folderId, String userName, String fileName) {
		if (fileRepository.findByFolderIdAndUserNameAndFileName(
			folderId, userName, fileName).isPresent()) {
			throw ErrorCd.DUPLICATE_FILE.serviceException();
		}
	}

	/**
	 * 파일이 서버에 저장될 이름을 반환한다.
	 * @param fileOriginalName 파일의 원래 이름
	 * @return 파일이 서버에 저장될 중복되지 않는 이름
	 */
	private String getFileName(String fileOriginalName) {
		return UUID.randomUUID() + fileOriginalName;
	}

	/**
	 * 파일의 Path(root/사용자 이름/파일 저장소 이름)를 생성한다.
	 * @param userName        사용자 이름
	 * @param fileStorageName 파일 저장소 이름
	 * @return 폴더 Path
	 */
	private Path getFilePath(String userName, String fileStorageName) {
		return storagePathService.createPathByUser(userName).resolve(fileStorageName);
	}

	/**
	 * 파일이 DB에 존재하는지 확인
	 * @param fileStorageName 파일 저장소 이름
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

	/**
	 * 파일 자체를 반환한다.
	 * @param filePath 파일이 존재하는 경로
	 * @return 파일 자체(Resource)
	 */
	private Resource getFileResource(Path filePath) {
		Resource file = resourceLoader.getResource("file:" + filePath.toString());
		if (!file.exists()) {
			throw ErrorCd.FILE_NOT_EXIST.serviceException(
				"[downloadFile] file doesn't exist - filePath {}", filePath.toString());
		}
		return file;
	}


	public void moveFile(long fileId, String userName, long targetFolderId) {

	}
}
