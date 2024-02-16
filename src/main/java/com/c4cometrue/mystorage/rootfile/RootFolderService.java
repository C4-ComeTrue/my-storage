package com.c4cometrue.mystorage.rootfile;

import com.c4cometrue.mystorage.exception.ErrorCode;
import com.c4cometrue.mystorage.util.FolderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
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

        Path path = Paths.get(storagePath, storedFolderName);

        RootFolderMetadata metadata = RootFolderMetadata.builder()
                .originalFileName(userFolderName)
                .storedFileName(storedFolderName)
                .ownerId(userId)
                .filePath(path.toString())
                .build();

        FolderUtil.createFolder(path);
        rootFolderRepository.save(metadata);
    }

    private void checkValidateBy(String storedFolderName, long userId, String userFolderName) {
        checkDuplicateBy(storedFolderName);
        checkDuplicateBy(userId, userFolderName);
    }

    private void checkDuplicateBy(Long userId, String userFolderName) {
        if (rootFolderRepository.existsByOwnerIdAndOriginalFileName(userId, userFolderName)) {
            throw ErrorCode.DUPLICATE_FOLDER_NAME.serviceException();
        }
    }

    private void checkDuplicateBy(String storedFolderName) {
        if (rootFolderRepository.existsByStoredFileName(storedFolderName)) {
            throw ErrorCode.DUPLICATE_SERVER_FOLDER_NAME.serviceException();
        }
    }
}
