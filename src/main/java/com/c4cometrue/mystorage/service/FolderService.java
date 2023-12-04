package com.c4cometrue.mystorage.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.response.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.FileMetaDataRes;
import com.c4cometrue.mystorage.dto.response.FolderMetaDataRes;
import com.c4cometrue.mystorage.dto.response.FolderOverviewRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;

	/**
	 * 특정 폴더의 대략적인 정보 반환
	 * @param folderId 해당 폴더 기본키
	 * @param userName 사용자 이름
	 * @return {@link com.c4cometrue.mystorage.dto.response.FolderOverviewRes}
	 */
	public FolderOverviewRes getFolderData(long folderId, String userName) {
		// 폴더 정보 조회
		FolderMetaData folder = getFolderInfo(folderId);
		// 권한 확인
		checkOwner(folder.getUserName(), userName);

		return new FolderOverviewRes(folderId, folder.getFolderName(), userName,
			getFiles(folderId), getFolders(folderId));
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
	private void checkOwner(String ownerName, String userName) {
		if (!ownerName.equals(userName)) {
			throw ErrorCd.NO_PERMISSION.serviceException();
		}
	}

	/**
	 * 폴더 생성
	 * @param parentFolderId 부모 폴더 기본키
	 * @param userName 사용자 이름
	 * @param folderName 생성할 폴더 이름
	 * @return {@link com.c4cometrue.mystorage.dto.response.CreateFolderRes}
	 */
	public CreateFolderRes createFolder(long parentFolderId, String userName, String folderName) {
		// 중복 폴더 존재 확인
		checkDuplicateFolder(folderName, parentFolderId, userName);

		// 폴더 메타 데이터 DB 저장
		FolderMetaData folderMetaData = FolderMetaData.builder()
			.folderName(folderName)
			.userName(userName)
			.parentFolderId(parentFolderId)
			.build();

		folderMetaData = folderRepository.save(folderMetaData);

		return new CreateFolderRes(folderMetaData.getFolderId(), folderName, userName);
	}

	/**
	 * 사용자 이름, 폴더 이름, 부모 폴더 기본키로 특정 폴더가 중복인지 확인
	 * @param userName 사용자 이름
	 * @param folderName 폴더 이름
	 * @param parentFolderId 부모 폴더 기본키
	 */
	private void checkDuplicateFolder(String folderName, long parentFolderId, String userName) {
		if (folderRepository.findByFolderNameAndParentFolderIdAndUserName(folderName, parentFolderId, userName)
			.isPresent()) {
			throw ErrorCd.DUPLICATE_FOLDER.serviceException();
		}
	}

	/**
	 * 폴더 이름 업데이트
	 * @param folderId 폴더 기본키
	 * @param parentFolderId 부모 폴더 기본키
	 * @param userName 사용자 이름
	 * @param newFolderName 새로운 폴더 이름
	 */
	@Transactional
	public void updateFolderName(long folderId, long parentFolderId, String userName, String newFolderName) {
		// 폴더 존재 여부 확인
		FolderMetaData folder = getFolderInfo(folderId);

		// 바꿀 권한이 있는가?
		checkOwner(folder.getUserName(), userName);

		// 바꿀 폴더 이름이 이미 같은 위치에 존재하는가
		checkDuplicateFolder(newFolderName, parentFolderId, userName);

		// 폴더명 변경
		folder.setFolderName(newFolderName);
		folderRepository.save(folder);
	}

	/**
	 * 특정 폴더의 자식 폴더들의 목록을 반환한다.
	 * @param folderId 특정 폴더 기본키
	 * @return 자식 폴더 목록
	 */
	private List<FileMetaDataRes> getFiles(long folderId) {
		Optional<List<FileMetaData>> fileList = fileRepository.findAllByFolderId(folderId);

		return fileList.map(files ->
				files.stream().map(fileMetaData -> new FileMetaDataRes(
					fileMetaData.getFileStorageName(),
					fileMetaData.getSize(),
					fileMetaData.getMime(),
					fileMetaData.getUserName())).toList())
			.orElseGet(LinkedList::new);
	}

	/**
	 * 특정 폴더의 자식 폴더들의 목록을 반환한다.
	 * @param folderId 특정 폴더 기본키
	 * @return 자식 폴더 목록
	 */
	private List<FolderMetaDataRes> getFolders(long folderId) {
		Optional<List<FolderMetaData>> folderList = folderRepository.findAllByParentFolderId(folderId);

		return folderList.map(folders ->
				folders.stream().map(folderMetaData -> new FolderMetaDataRes(
					folderMetaData.getFolderId(),
					folderMetaData.getFolderName(),
					folderMetaData.getUserName())).toList())
			.orElseGet(LinkedList::new);
	}
}
