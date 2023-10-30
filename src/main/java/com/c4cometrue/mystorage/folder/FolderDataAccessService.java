package com.c4cometrue.mystorage.folder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderDataAccessService implements FolderReader, FolderWriter {
	private final FolderRepository folderRepository;
	@Value("${file.storage-path}")
	private String storagePath;

	public String findPathBy(Long parentId) {
		return parentId == null ? storagePath : findBy(parentId).getFilePath();
	}

	// 적절한 예외 던져라
	public FolderMetadata findBy(Long folderId) {
		return folderRepository.findById(folderId).orElseThrow(
			() -> ErrorCode.CANNOT_FOUND_FOLDER.serviceException("folderId { }", folderId)
		);
	}

	public void verifyBy(Long parentId, Long userId) {
		if (parentId == null) {
			return;
		}

		if (!folderRepository.existsByParentIdAndUserId(parentId, userId)) {
			throw ErrorCode.UNAUTHORIZED_FOLDER_ACCESS.serviceException();
		}
	}

	public void validateFolderOwnershipBy(Long folderId, Long userId) {
		if (!folderRepository.existsByIdAndUserId(folderId, userId)) {
			throw ErrorCode.CANNOT_FOUND_FOLDER.serviceException();
		}
	}

	// 자신의 폴더나 null 밑에서만 폴더를 만들수 있어야 한다
	public void persist(String userFolderName, String storedFolderName, String path, Long userId, Long parentId) {
		// 부모 폴더 주인이 가 유저와 같거나 부모 폴더 id 가 널이여야지 가능

		verifyBy(parentId, userId);
		// 중복된 폴더 이름 생성은 불가능
		checkDuplicateBy(userFolderName, parentId, userId);

		FolderMetadata metadata = FolderMetadata.builder()
			.originalFolderName(userFolderName)
			.storedFolderName(storedFolderName)
			.filePath(path)
			.parentId(parentId)
			.userId(userId)
			.build();

		folderRepository.save(metadata);
	}

	public void checkDuplicateBy(String userFolderName, Long parentId, Long userId) {
		if (folderRepository.existsByParentIdAndUserIdAndOriginalFolderName(parentId, userId, userFolderName)) {
			throw ErrorCode.DUPLICATE_FOLDER_NAME.serviceException();
		}
	}

	public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
		validateFolderOwnershipBy(folderId, userId);
		changeFolderNameBy(folderName, folderId);
	}

	public void changeFolderNameBy(String folderName, Long folderId) {
		FolderMetadata folderMetadata = findBy(folderId);
		folderMetadata.changeFolderName(folderName);
		folderRepository.save(folderMetadata);
	}
}
