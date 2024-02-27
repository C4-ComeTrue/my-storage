package com.c4cometrue.mystorage.folder;

import com.c4cometrue.mystorage.rootfile.RootFolderService;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.c4cometrue.mystorage.folder.dto.FolderSummaryRes;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.c4cometrue.mystorage.folder.dto.CursorFolderResponse;
import com.c4cometrue.mystorage.folder.dto.FolderContent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
    private final FolderDataHandlerService folderDataHandlerService;
    private final RootFolderService rootFolderService;

    public void createBy(Long userId, String userFolderName, Long parentId, Long rootId) {
        rootFolderService.checkValidateBy(rootId, userId);
        String storedFolderName = FolderMetadata.storedName(userFolderName);
        String parentPath = folderDataHandlerService.findPathBy(parentId, userId);

        Path path = Paths.get(parentPath, storedFolderName);

        folderDataHandlerService.persist(userFolderName, storedFolderName, path.toString(), userId, parentId, rootId);
    }

    public void changeFolderNameBy(String folderName, Long folderId, Long userId) {
        folderDataHandlerService.changeFolderNameBy(folderName, folderId, userId);
    }

    public String findPathBy() {
        return folderDataHandlerService.findPathBy();
    }

    public CursorFolderResponse getFolders(Long parentId, Long cursorId, Long userId, Pageable page) {
        List<FolderMetadata> folders = folderDataHandlerService.getFolderList(parentId, cursorId, userId, page);
        List<FolderContent> folderContents = folders.stream()
            .map(folder -> FolderContent.of(folder.getId(), folder.getOriginalFolderName()))
            .toList();
        Long lastIdOfList = folders.isEmpty() ? null : folders.get(folders.size() - 1).getId();
        return CursorFolderResponse.of(folderContents,
            folderDataHandlerService.hasNext(parentId, userId, lastIdOfList));
    }

    public void validateBy(Long folderId, Long userId) {
        folderDataHandlerService.validateFolderOwnershipBy(folderId, userId);
    }

    public void moveFolder(Long folderId, Long userId, Long destinationFolderId) {
        // 해당 폴더가 접근 가능 한 폴더 인지 체크
        validateBy(destinationFolderId, userId);

        FolderMetadata folderMetadata = folderDataHandlerService.findBy(folderId, userId);

        folderMetadata.changeParentId(destinationFolderId);

        folderDataHandlerService.persist(folderMetadata);

    }

    public void deleteFolder(FolderMetadata folderMetadata) {
        folderDataHandlerService.delete(folderMetadata);
    }


    public List<FolderMetadata> findAllBy(Long parentId) {
        return folderDataHandlerService.findAllBy(parentId);
    }

    public FolderMetadata findBy(long folderId, long userId) {
        return folderDataHandlerService.findBy(folderId, userId);
    }

    public FolderSummaryRes getFolderSummary(Long folderId, Long userId) {
        validateBy(folderId, userId);
        FolderMetadata folder = folderDataHandlerService.findBy(folderId, userId);
        return FolderSummaryRes.of(folder.getOriginalFolderName(), folder.getCreatedAt(), folder.getUpdatedAt());
    }
}
