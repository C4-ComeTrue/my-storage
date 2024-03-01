package com.c4cometrue.mystorage.service;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.response.file.FileMetaDataRes;
import com.c4cometrue.mystorage.dto.response.folder.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.folder.FolderMetaDataRes;
import com.c4cometrue.mystorage.dto.response.folder.FolderOverviewRes;
import com.c4cometrue.mystorage.entity.DeleteLog;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.DeleteLogRepository;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	private final DeleteLogRepository deleteLogRepository;
	private static final int PAGE_SIZE = 50;

	/**
	 * 특정 폴더의 대략적인 정보 반환
	 * @param folderId 해당 폴더 기본키
	 * @param userName 사용자 이름
	 * @return {@link FolderOverviewRes}
	 */
	public FolderOverviewRes getFolderTotalInfo(long folderId, String userName) {
		// 폴더 정보 조회
		FolderMetaData folder = getFolderInfo(folderId);
		// 권한 확인
		isOwner(folder.getUserName(), userName);

		return new FolderOverviewRes(folderId, folder.getFolderName(), userName,
			getFiles(folderId, 0), getFolders(folderId, 0));
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
	 * 폴더 생성
	 * @param parentFolderId 부모 폴더 기본키
	 * @param userName 사용자 이름
	 * @param folderName 생성할 폴더 이름
	 * @return {@link CreateFolderRes}
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

		return new CreateFolderRes(folderMetaData.getFolderId(), folderName);
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

		// 권한 확인
		isOwner(folder.getUserName(), userName);

		// 바꿀 폴더 이름이 이미 같은 위치에 존재하면 예외 처리
		checkDuplicateFolder(newFolderName, parentFolderId, userName);

		// 폴더명 변경
		folder.setFolderName(newFolderName);
	}

	/**
	 * 폴더에 포함된 파일들을 pageSize만큼 반환한다.
	 * @param folderId 특정 폴더 기본키
	 * @param page 페이지 번호
	 * @return 파일 목록
	 */
	public List<FileMetaDataRes> getFiles(long folderId, int page) {
		Page<FileMetaData> filePage = fileRepository.findAllByFolderId(folderId, PageRequest.of(page, 50));
		Page<FileMetaData> filePage = fileRepository.findAllByFolderId(folderId, PageRequest.of(page, PAGE_SIZE));
		LinkedList<FileMetaDataRes> result = new LinkedList<>();

		for (FileMetaData fileMetaData : filePage) {
			result.add(new FileMetaDataRes(fileMetaData.getFileStorageName(), fileMetaData.getSize(),
				fileMetaData.getMime(), fileMetaData.getUserName()));
		}
		return result;
	}

	/**
	 * 폴더의 하위 폴더들을 pageSize만큼 반환한다.
	 * @param folderId 특정 폴더 기본키
	 * @param page 페이지 번호
	 * @return 하위 폴더 목록
	 */
	public List<FolderMetaDataRes> getFolders(long folderId, int page) {
		Page<FolderMetaData> folderList = folderRepository.findAllByParentFolderId(folderId,
			PageRequest.of(page, PAGE_SIZE));
		LinkedList<FolderMetaDataRes> result = new LinkedList<>();

		for (FolderMetaData folderMetaData : folderList) {
			result.add(new FolderMetaDataRes(folderMetaData.getFolderId(), folderMetaData.getFolderName(),
				folderMetaData.getUserName()));
		}
		return result;
	}

	/**
	 * 폴더를 삭제하고, 해당 폴더의 모든 하위 폴더와 파일들을 삭제한다.
	 * @param folderId 폴더 기본키
	 */
	@Transactional
	public void deleteFolder(long folderId, String userName) {
		// folderId 권한 확인 후 삭제
		FolderMetaData folder = getFolderInfo(folderId);
		isOwner(folder.getUserName(), userName);
		folderRepository.delete(folder);

		// 하위 폴더 삭제 BFS
		ArrayDeque<Long> folderQueue = new ArrayDeque<>();
		folderQueue.add(folderId);

		while (!folderQueue.isEmpty()) {
			long parentFolderId = folderQueue.removeFirst();
			// 해당 폴더의 모든 파일 메타 데이터 삭제
			deleteAllFile(parentFolderId);
			// 해당 폴더의 자식 폴더들 조회
			Optional<List<FolderMetaData>> children = folderRepository.findAllByParentFolderId(parentFolderId);
			if (children.isPresent()) {
				folderRepository.deleteAllInBatch(children.get());
				for (FolderMetaData folderMetaData : children.get()) {
					folderQueue.add(folderMetaData.getFolderId());
				}
			}
		}
	}

	/**
	 * 특정 폴더에 속한 모든 파일 메타 데이터를 삭제한다.
	 * @param folderId 폴더 기본키
	 */
	@Transactional
	public void deleteAllFile(long folderId) {
		Optional<List<FileMetaData>> fileList = fileRepository.findAllByFolderId(folderId);

		if (fileList.isPresent()) {
			List<DeleteLog> deleteLogs = new LinkedList<>();
			for (FileMetaData fileMetaData : fileList.get()) {
				deleteLogs.add(new DeleteLog(fileMetaData.getFileStorageName()));
			}
			deleteLogRepository.saveAll(deleteLogs);
			fileRepository.deleteAllInBatch(fileList.get());
		}
	}

	/**
	 * 폴더를 이동한다.
	 * 논리적으로는 하위의 모든 폴더와 파일이 이동해야 하지만 일일이 수정 할 필요가 없다.
	 * 하위 폴더나 파일이나, 다 이동할 'folderId'를 보고 있기 때문에, 'folderId'를 조회할 때 어차피 따라온다.
	 * 'folderId'에게 화살표를 가리키고 있기 때문에, 'folderId'가 어디로 이동하던 상관 없는 것이다.
	 * 그러므로 'folderId'의 부모 폴더 pk만 바꾼다.
	 * @param folderId 이동하는 폴더
	 * @param targetFolderId 이동할 위치의 폴더 기본키
	 */
	@Transactional
	public void moveFolder(long folderId, long targetFolderId, String userName) {
		// 이동할 폴더가 존재하는가
		FolderMetaData folder = getFolderInfo(folderId);
		// 이동시킬 권한을 가졌는가
		isOwner(folder.getUserName(), userName);
		// 이동할 수 있는가 (targetFolder의 부모에 folderId가 없는가)
		checkFolderRelationship(folderId, targetFolderId);
		// 이동
		folder.setParentFolderId(targetFolderId);
	}

	/**
	 * targetFolderId가 folderId의 하위 폴더인지 확인한다.
	 * folderId가 targetFolderId의 하위 폴더라면 폴더 이동이 가능하므로 문제가 없다.
	 * @param folderId 움직일 폴더의 아이디
	 * @param targetFolderId 도착 지점의 폴더 아이디
	 */
	private void checkFolderRelationship(long folderId, long targetFolderId) {
		long nowFolderId = targetFolderId;

		// 0은 root 값이며, 이에 도달하면 종료한다.
		while (nowFolderId != 0) {
			nowFolderId = folderRepository.findParentFolderIdByFolderId(nowFolderId);
			if (nowFolderId == folderId) {
				// 만약 부모 - 자식 관계에 있다면 경로에 사이클이 생긴다.
				throw ErrorCd.FOLDER_CANT_BE_MOVED.serviceException();
			}
		}
	}
}
