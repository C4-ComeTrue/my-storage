package com.c4cometrue.mystorage.rootfile;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.rootfile.dto.RootInfo;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import com.c4cometrue.mystorage.util.FolderUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class RootFolderService {
    private final RootFolderRepository rootFolderRepository;

    @Value("${file.storage-path}")
    private String storagePath;

    public void createBy(long userId, String userFolderName) {
        String storedFolderName = RootFolderMetadata.storedName();

        checkValidateBy(storedFolderName, userId, userFolderName);

        Path path = Paths.get(storagePath);

        RootFolderMetadata metadata =
            RootFolderMetadata.builder().originalFolderName(userFolderName).storedFolderName(storedFolderName)
                .ownerId(userId).filePath(path.toString()).build();

        FolderUtil.createFolder(path);
        rootFolderRepository.save(metadata);
    }

    private void checkValidateBy(String storedFolderName, long userId, String userFolderName) {
        checkDuplicateBy(storedFolderName);
        checkDuplicateBy(userId, userFolderName);
    }

    public RootFolderMetadata getRootFolderMetadata(Long userId, Long rootId) {
        return rootFolderRepository.findByOwnerIdAndId(userId, rootId)
            .orElseThrow(ErrorCode.CANNOT_FOUND_FOLDER::serviceException);
    }

    private void checkDuplicateBy(Long userId, String userFolderName) {
        if (rootFolderRepository.existsByOwnerIdAndOriginalFolderName(userId, userFolderName)) {
            throw ErrorCode.DUPLICATE_FOLDER_NAME.serviceException();
        }
    }

    private void checkDuplicateBy(String storedFolderName) {
        if (rootFolderRepository.existsByStoredFolderName(storedFolderName)) {
            throw ErrorCode.DUPLICATE_SERVER_FOLDER_NAME.serviceException();
        }
    }

    public void updateUsedSpaceForUpload(Long userId, Long rootId, BigDecimal fileSize) {
        RootFolderMetadata rootFolderMetadata = getRootFolderMetadata(userId, rootId);
        rootFolderMetadata.increaseUsedSpace(fileSize);
    }

    public void updateUsedSpaceForDeletion(Long userId, Long rootId, BigDecimal fileSize) {
        RootFolderMetadata rootFolderMetadata = getRootFolderMetadata(userId, rootId);
        rootFolderMetadata.decreaseUsedSpace(fileSize);
    }

    public RootInfo getRootInfo(long rootId, long userId) {
        RootFolderMetadata metadata = getRootFolderMetadata(userId, rootId);

        String folderName = metadata.getOriginalFolderName();
        BigDecimal availableSpaceInGb = DataSizeConverter.bytesToGigaBytes(metadata.getAvailableSpace());
        BigDecimal usedSpaceInGb = DataSizeConverter.bytesToGigaBytes(metadata.getUsedSpace());
        BigDecimal remainingSpace = metadata.calRemainSpace();
        BigDecimal remainingSpaceInGb = DataSizeConverter.bytesToGigaBytes(remainingSpace);
        return RootInfo.of(rootId, folderName, availableSpaceInGb, usedSpaceInGb, remainingSpaceInGb);
    }

    public void checkValidateBy(Long rootId, Long userId) {
        if (rootFolderRepository.existsByIdAndOwnerId(rootId, userId)) {
            throw ErrorCode.CANNOT_FOUND_FOLDER.serviceException();
        }
    }
}
