package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.dto.request.CreateFolderReq;
import com.c4cometrue.mystorage.dto.request.GetFolderReq;
import com.c4cometrue.mystorage.dto.request.UpdateFolderNameReq;
import com.c4cometrue.mystorage.dto.response.CreateFolderRes;
import com.c4cometrue.mystorage.dto.response.FileMetaDataRes;
import com.c4cometrue.mystorage.dto.response.FolderMetaDataRes;
import com.c4cometrue.mystorage.dto.response.FolderOverviewRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.entity.FolderMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.repository.FolderRepository;
import com.c4cometrue.mystorage.util.FileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class FolderService {
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	private final StoragePathService storagePathService;

	public FolderOverviewRes getFolderData(GetFolderReq getFolderReq) {
		// parentFolder가 없다면? -> Error (잘못된 요청)
		checkParentFolder(getFolderReq.parentFolderId());

		// 폴더 존재 여부 확인
		FolderMetaData folder = checkFolder(getFolderReq.userName(), getFolderReq.folderName(),
			getFolderReq.parentFolderId()).orElseThrow(ErrorCd.FOLDER_NOT_EXIST::serviceException);

		return new FolderOverviewRes(folder.getFolderId(), folder.getFolderName(), folder.getUserName(),
			getFiles(folder),
			getFolders(folder));
	}

	public CreateFolderRes createFolder(CreateFolderReq createFolderReq) {
		// parentFolder가 없다면? -> Error (잘못된 요청)
		FolderMetaData parentFolder = checkParentFolder(createFolderReq.parentFolderId());

		// 중복 폴더 존재 -> Error
		String folderName = createFolderReq.folderName();
		String userName = createFolderReq.userName();
		if (checkFolder(userName, folderName, createFolderReq.parentFolderId()).isPresent()) {
			throw ErrorCd.DUPLICATE_FOLDER.serviceException();
		}

		// 경로 생성 (부모 폴더 경로 + 폴더 이름)
		Path folderPath = storagePathService.createFolderPath(parentFolder.getFolderPath(),
			folderName);
		// 물리적 폴더 생성
		FileUtil.createFolder(folderPath);

		// 폴더 메타 데이터 DB 저장
		FolderMetaData folderMetaData = FolderMetaData.builder()
			.folderName(folderName)
			.folderPath(folderPath.toString())
			.userName(userName)
			.parentFolderId(createFolderReq.parentFolderId())
			.build();

		folderRepository.save(folderMetaData);

		return new CreateFolderRes(folderMetaData.getFolderId(), folderName, userName);
	}

	@Transactional
	public void updateFolderName(UpdateFolderNameReq updateFolderNameReq) {
		// 부모 폴더가 존재 하는가
		FolderMetaData parentFolder = checkParentFolder(updateFolderNameReq.parentFolderId());
		val userName = updateFolderNameReq.userName();
		val parentFolderId = updateFolderNameReq.parentFolderId();

		// 폴더 존재 여부 확인
		FolderMetaData folder = checkFolder(userName, updateFolderNameReq.folderName(), parentFolderId)
			.orElseThrow(ErrorCd.FOLDER_NOT_EXIST::serviceException);

		// 바꿀 폴더 이름이 존재하는가
		if (checkFolder(userName, updateFolderNameReq.newFolderName(), parentFolderId).isPresent()) {
			throw ErrorCd.DUPLICATE_FOLDER.serviceException("[updateFolderName] {} is already exist",
				updateFolderNameReq.newFolderName());
		}

		// 폴더명 변경
		Path oldPath = Path.of(folder.getFolderPath());
		Path newPath = storagePathService.createFolderPath(parentFolder.getFolderPath(),
			updateFolderNameReq.newFolderName());

		folder.setFolderPath(newPath.toString());
		folder.setFolderName(updateFolderNameReq.newFolderName());
		folderRepository.save(folder);

		// 하위 폴더 경로 변경
		LinkedList<FolderInfo> folderIdList = new LinkedList<>();
		FolderInfo init = new FolderInfo(folder.getFolderId(), folder.getFolderPath());
		folderIdList.add(init);

		// BFS
		while (!folderIdList.isEmpty()) {
			FolderInfo now = folderIdList.removeFirst();
			Optional<List<FolderMetaData>> children = folderRepository.findAllByParentFolderId(now.folderId);
			if (children.isPresent()) {
				for (FolderMetaData child : children.get()) {
					Path childNewPath = storagePathService.createFolderPath(folder.getFolderPath(),
						child.getFolderName());
					child.setFolderPath(childNewPath.toString());
					folderIdList.add(new FolderInfo(child.getFolderId(), child.getFolderPath()));
					folderRepository.save(child);
				}
			}
		}

		// 물리적 파일명 변환
		FileUtil.renameFolder(oldPath, newPath);
	}

	private List<FileMetaDataRes> getFiles(FolderMetaData folder) {
		List<FileMetaDataRes> fileResult = new LinkedList<>();
		Optional<List<FileMetaData>> fileList = fileRepository.findAllByFolderId(folder.getFolderId());

		if (fileList.isPresent()) {
			for (FileMetaData fileMetaData : fileList.get()) {
				fileResult.add(new FileMetaDataRes(
					fileMetaData.getFileStorageName(),
					fileMetaData.getSize(),
					fileMetaData.getMime(),
					fileMetaData.getUserName()));
			}
		}

		return fileResult;
	}

	private List<FolderMetaDataRes> getFolders(FolderMetaData folder) {
		List<FolderMetaDataRes> folderResult = new LinkedList<>();
		Optional<List<FolderMetaData>> folderList = folderRepository.findAllByParentFolderId(folder.getFolderId());
		if (folderList.isPresent()) {
			for (FolderMetaData folderMetaData : folderList.get()) {
				folderResult.add(new FolderMetaDataRes(
					folder.getFolderId(),
					folderMetaData.getFolderName(),
					folderMetaData.getUserName()));
			}
		}
		return folderResult;
	}

	private Optional<FolderMetaData> checkFolder(String userName, String folderName, long parentFolderId) {
		return folderRepository.findByUserNameAndFolderNameAndParentFolderId(
			userName, folderName, parentFolderId);
	}

	private FolderMetaData checkParentFolder(long parentFolderId) {
		return folderRepository.findByFolderId(parentFolderId).orElseThrow(
			() -> ErrorCd.FOLDER_NOT_EXIST.serviceException("parent folder doesn't exist")
		);
	}

	private static class FolderInfo {
		long folderId;
		String folderPath;

		FolderInfo(long folderId, String folderPath) {
			this.folderId = folderId;
			this.folderPath = folderPath;
		}
	}
}
