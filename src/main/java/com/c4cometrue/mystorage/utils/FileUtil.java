package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private final ResourceLoader resourceLoader;

    public void uploadFile(MultipartFile file, String fileUploadPath) {
        Path destinationPath = Path.of(fileUploadPath);

        try {
            file.transferTo(destinationPath);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
        }
    }

    public Resource downloadFile(String fileUploadPath) {
        val resource = resourceLoader.getResource(fileUploadPath);

        if (!resource.exists() || !resource.isReadable()) {
            throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED);
        }

        return resource;
    }

    public void deleteFile(String fileUploadPath) {
        Path destinationPath = Path.of(fileUploadPath);

        try {
            Files.delete(destinationPath);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED, ex);
        }
    }

}
