package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

@Component
@Slf4j
public class FileUtil {

    private FileUtil() {}

    private static final String URL_PROTOCOL = "file:";

    public static void uploadFile(MultipartFile file, String fileUploadPath) {
        Path destinationPath = Path.of(fileUploadPath);

        try {
            file.transferTo(destinationPath);
            log.info("file upload success : {}", fileUploadPath);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
        }
    }

    public static Resource downloadFile(String fileUploadPath) {
        try {
            val resource = new UrlResource(URL_PROTOCOL + fileUploadPath);

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED);
            }

            log.info("file download success : {}", fileUploadPath);
            return resource;
        } catch (MalformedURLException ex) {
            throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED, ex);
        }
    }
}
