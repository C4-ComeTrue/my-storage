package com.c4cometrue.mystorage.rootfolder;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.rootfolder.dto.RootInfo;
import com.c4cometrue.mystorage.util.DataSizeConverter;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RootFolderService {
    private final RootFolderRepository rootFolderRepository;

    public void createBy(long userId, String userFolderName) {
        String storedFolderName = RootFolderMetadata.storedName();

        checkValidateBy(storedFolderName, userId, userFolderName);

        RootFolderMetadata metadata = RootFolderMetadata.builder()
            .originalFolderName(userFolderName)
            .storedFolderName(storedFolderName)
            .ownerId(userId)
            .build();

        rootFolderRepository.save(metadata);
    }

    private void checkValidateBy(String storedFolderName, long userId, String userFolderName) {
        checkDuplicateBy(storedFolderName);
        checkDuplicateBy(userId, userFolderName);
    }

    private void checkDuplicateBy(String storedFolderName) {
        if (rootFolderRepository.existsByStoredFolderName(storedFolderName)) {
            throw ErrorCode.DUPLICATE_SERVER_FOLDER_NAME.serviceException();
        }
    }

    private void checkDuplicateBy(Long userId, String userFolderName) {
        if (rootFolderRepository.existsByOwnerIdAndOriginalFolderName(userId, userFolderName)) {
            throw ErrorCode.DUPLICATE_FOLDER_NAME.serviceException();
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

    private RootFolderMetadata getRootFolderMetadata(Long userId, Long rootId) {
        return rootFolderRepository.findByIdAndOwnerId(rootId, userId)
            .orElseThrow(ErrorCode.CANNOT_FOUND_FOLDER::serviceException);
    }

    public void checkValidateBy(Long rootId, Long userId) {
        if (!rootFolderRepository.existsByIdAndOwnerId(rootId, userId)) {
            throw ErrorCode.CANNOT_FOUND_FOLDER.serviceException();
        }
    }
}
