package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.response.file.FileDownloadRes;
import com.c4cometrue.mystorage.dto.response.file.FileMetaDataRes;
import com.c4cometrue.mystorage.entity.DeleteLog;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.DeleteLogRepository;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	private final ResourceLoader resourceLoader;
	private final StoragePathService storagePathService;
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	private final DeleteLogRepository deleteLogRepository;

	/**
	 * 파일 업로드
	 * @param file     업로드할 파일
	 * @param userName 사용자 이름
	 * @param folderId 파일이 업로드 될 폴더 기본키
	 * @return 파일의 메타 데이터
	 */
	@Transactional
	public FileMetaDataRes uploadFile(MultipartFile file, String userName, long folderId) {
		// 폴더 유무 및 권한 확인
		checkFolder(folderId, userName);
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
	 * 폴더가 DB에 존재하는지 확인하고, 소유자를 확인한다.
	 * @param folderId 폴더 기본키
	 * @param userName 사용자 이름
	 */
	private void checkFolder(long folderId, String userName) {
		FolderMetaData folderMetaData = getFolderInfo(folderId);
		isOwner(folderMetaData.getUserName(), userName);
	}

	/**
	 * 폴더 기본키로 특정 폴더가 존재하는지 확인한다.
	 * @param folderId 폴더 기본키
	 * @return 해당 폴더 메타 데이터
	 */
	private FolderMetaData getFolderInfo(long folderId) {
		return folderRepository.findByFolderId(folderId).orElseThrow(ErrorCd.FOLDER_NOT_EXIST::serviceException);
	}

	/**
	 * 폴더의 주인이 일치하는지 확인한다.
	 * @param ownerName 실제 소유자 이름
	 * @param userName 요청한 사용자 이름
	 */
	private void isOwner(String ownerName, String userName) {
		if (!ownerName.equals(userName)) {
			throw ErrorCd.NO_PERMISSION.serviceException();
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
	 * 파일 삭제
	 * @param fileId 파일 기본키
	 * @param userName 사용자 이름
	 * @param folderId 파일이 존재하는 폴더 기본키
	 */
	@Transactional
	public void deleteFile(long fileId, String userName, long folderId) {
		// 폴더 유무 및 권한 확인
		checkFolder(folderId, userName);
		// 파일 메타 데이터 조회 및 권한 확인
		FileMetaData fileMetaData = getFileMetaData(fileId, userName);
		// 파일 DB 정보 삭제
		fileRepository.delete(fileMetaData);
		// 파일 물리적 삭제 예약
		deleteLogRepository.save(new DeleteLog(fileMetaData.getFileStorageName()));
	}

	/**
	 * 파일이 DB에 존재하는지 확인하고 파일의 주인이 요청한 사용자명과 일치하는지 확인한다.
	 * @param fileId 파일 기본 키
	 * @param userName 사용자 이름
	 * @return 파일 메타 데이터
	 */
	FileMetaData getFileMetaData(long fileId, String userName) {
		FileMetaData fileMetaData = fileRepository.findById(fileId)
			.orElseThrow(() -> ErrorCd.FILE_NOT_EXIST
				.serviceException("[getFileMetaData] file not exist - fileId: {}", fileId));
		isOwner(fileMetaData.getUserName(), userName);
		return fileMetaData;
	}


	/**
	 * 파일 다운로드
	 * @param fileId 파일 기본키
	 * @param userName 사용자 이름
	 * @param folderId 삭제할 파일이 존재하는 폴더 기본키
	 * @return 파일 Resource 데이터 및 메타 데이터
	 */
	public FileDownloadRes downloadFile(long fileId, String userName, long folderId) {
		// 폴더 유무 및 권한 확인
		checkFolder(folderId, userName);
		// 파일 메타 데이터 조회 및 권한 확인
		FileMetaData fileMetaData = getFileMetaData(fileId, userName);
		// 파일 물리적 경로
		Path filePath = getFilePath(userName, fileMetaData.getFileStorageName());
		return new FileDownloadRes(getFileResource(filePath), fileMetaData.getFileName(), fileMetaData.getMime());
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

	/**
	 * 해당 파일의 folderId를 변경한다. 물리적으로 파일을 옮기지 않는다.
	 * @param fileId 해당 파일의 기본키
	 * @param targetFolderId 이동할 폴더의 기본키
	 */
	@Transactional
	public void moveFile(long fileId, long targetFolderId, String userName) {
		// 파일 메타 데이터 조회 및 권한 확인
		FileMetaData fileMetaData = getFileMetaData(fileId, userName);

		// 폴더 유무 및 권한 확인
		checkFolder(targetFolderId, userName);

		// 파일의 폴더 값 업데이트
		fileMetaData.setFolderId(targetFolderId);
	}
}
