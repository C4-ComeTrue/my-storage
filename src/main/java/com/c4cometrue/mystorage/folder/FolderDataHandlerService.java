package com.c4cometrue.mystorage.folder;

import com.c4cometrue.mystorage.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderDataHandlerService {
    private final FolderRepository folderRepository;
    @Value("${file.storage-path}")
    private String storagePath;

    public String findPathBy() {
        return storagePath;
    }

    public String findPathBy(Long parentId, Long userId) {
        return parentId == null ? storagePath : findBy(parentId, userId).getFilePath();
    }

    // 자신의 폴더나 null 밑에서만 폴더를 만들수 있어야 한다
    public void persist(String userFolderName, String storedFolderName, String path, Long uploaderId, Long parentId) {
        // 부모 폴더 주인이 가 유저와 같거나 부모 폴더 id 가 널이여야지 가능
        validateFolderOwnershipBy(parentId, uploaderId);
        // 중복된 폴더 이름 생성은 불가능
        checkDuplicateBy(userFolderName, parentId, uploaderId);

        FolderMetadata metadata = FolderMetadata.builder()
                .originalFolderName(userFolderName)
                .storedFolderName(storedFolderName)
                .filePath(path)
                .parentId(parentId)
                .uploaderId(uploaderId)
                .build();

        folderRepository.save(metadata);
    }

    private void checkDuplicateBy(String userFolderName, Long parentId, Long userId) {
        if (folderRepository.existsByParentIdAndUploaderIdAndOriginalFolderName(parentId, userId, userFolderName)) {
            throw ErrorCode.DUPLICATE_FOLDER_NAME.serviceException();
        }
    }

    public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
        validateFolderOwnershipBy(folderId, userId);
        checkDuplicateBy(folderName, folderId, userId);
        changeFolderName(folderName, folderId, userId);
    }

    private void changeFolderName(String folderName, Long folderId, Long userId) {
        FolderMetadata folderMetadata = findBy(folderId, userId);
        folderMetadata.changeFolderName(folderName);
        folderRepository.save(folderMetadata);
    }

    public void validateFolderOwnershipBy(Long folderId, Long userId) {
        if (folderId != null && !folderRepository.existsByIdAndUploaderId(folderId, userId)) {
            throw ErrorCode.CANNOT_FOUND_FOLDER.serviceException();
        }
    }

    public FolderMetadata findBy(Long folderId, Long userId) {
        return folderRepository.findByIdAndUploaderId(folderId, userId)
                .orElseThrow(() -> ErrorCode.CANNOT_FOUND_FOLDER.serviceException("folderId { }", folderId));
    }

    public List<FolderMetadata> getFolderList(Long parentId, Long cursorId, Long userId, Pageable page) {
        return cursorId == null ? folderRepository.findAllByParentIdAndUploaderIdOrderByIdDesc(parentId, userId, page) :
                folderRepository.findByParentIdAndUploaderIdAndIdLessThanOrderByIdDesc(parentId, userId, cursorId, page);
    }

    public Boolean hasNext(Long parentId, Long userId, Long id) {
        return folderRepository.existsByParentIdAndUploaderIdAndIdLessThan(parentId, userId, id);
    }

    public void persist(FolderMetadata folderMetadata) {
        folderRepository.save(folderMetadata);
    }

    public List<FolderMetadata> findAllBy(Long parentId) {
        return folderRepository.findAllByParentId(parentId);
    }

    public void delete(FolderMetadata folderMetadata) {
        folderRepository.delete(folderMetadata);
    }
}
