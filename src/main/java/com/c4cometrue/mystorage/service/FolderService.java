package com.c4cometrue.mystorage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c4cometrue.mystorage.api.dto.FolderUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.domain.FileType;
import com.c4cometrue.mystorage.repository.FileMetaDataReader;
import com.c4cometrue.mystorage.repository.FileMetaDataWriter;
import com.c4cometrue.mystorage.utils.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FolderService {
	private final PathService pathService;
	private final FileUtil fileUtil;
	private final FileMetaDataReader folderReader;
	private final FileMetaDataWriter folderWriter;

	private static final String ROOT_PATH = "";

	public FolderUploadDto.Res createRootFolder(long userId, String name) {
		String fullPath = pathService.getFullFilePath(ROOT_PATH, name);

		// 이미 폴더가 존재하는지 확인
		folderReader.validateDuplicateFolder(name, userId, null);

		// 메타데이터 저장
		FileMetaData folder = folderWriter.saveFolderMetaData(userId, name, null);

		// 물리적인 루트 폴더 생성
		fileUtil.createFolder(fullPath);
		return new FolderUploadDto.Res(folder.getId());
	}

	public FolderUploadDto.Res createFolder(long userId, Long parentId, String name) {
		// 부모 폴더 유효성 검사
		FileMetaData parent = getFolder(parentId, userId);

		// 중복되는 이름이 같은 뎁스에 있는 경우 확인
		folderReader.validateDuplicateFolder(name, userId, parent);

		// 메타 데이터만 저장
		FileMetaData folder = folderWriter.saveFolderMetaData(userId, name, parent);
		return new FolderUploadDto.Res(folder.getId());
	}

	public void renameFolder(long userId, long folderId, String newName) {
		// 폴더 존재 여부 확인
		FileMetaData folder = getFolder(folderId, userId);

		// 변경하려는 이름이 중복되는지 확인
		folderReader.validateDuplicateFolder(newName, userId, folder.getParent());

		// 자식들 제외하고 변경하려는 폴더 자체만 이름 수정
		folder.rename(newName);
	}

	private FileMetaData getFolder(Long id, long userId) {
		FileMetaData folder = folderReader.get(id, userId);
		if (folder.getFileType() != FileType.FOLDER) {
			throw new BusinessException(ErrorCode.INVALID_FOLDER);
		}
		return folder;
	}

}
