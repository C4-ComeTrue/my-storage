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
		validateDuplicateFolderName(null, name);

		// 메타데이터 저장
		FileMetaData folder = folderWriter.saveFolderMetaData(userId, name, ROOT_PATH, null);

		// 물리적인 루트 폴더 생성
		fileUtil.createFolder(fullPath);
		return new FolderUploadDto.Res(folder.getId());
	}

	public FolderUploadDto.Res createFolder(long userId, Long parentId, String name) {
		// 부모 폴더 유효성 검사
		FileMetaData parent = getParentFolder(parentId, userId);

		// 중복되는 이름이 같은 뎁스에 있는 경우 확인
		validateDuplicateFolderName(parent, name);

		// 메타 데이터만 저장
		String folderPath = pathService.getFilePath(parent.getFolderPath(), parent.getFileName());
		FileMetaData folder = folderWriter.saveFolderMetaData(userId, name, folderPath, parent);
		return new FolderUploadDto.Res(folder.getId());
	}

	private FileMetaData getParentFolder(Long id, long userId) {
		FileMetaData folder = this.folderReader.get(id, userId);
		if (folder.getFileType() != FileType.FOLDER) {
			throw new BusinessException(ErrorCode.INVALID_FOLDER);
		}
		return folder;
	}

	private void validateDuplicateFolderName(FileMetaData folder, String name) {
		if (this.folderReader.isDuplicateFolderName(folder, name)) {
			throw new BusinessException(ErrorCode.DUPLICATE_FOLDER);
		}
	}
}
