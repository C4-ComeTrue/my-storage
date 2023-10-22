package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
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
    private static String uploadDir;

    @Value("${file.upload-dir}")
    public void setUploadDir(String value) {
        uploadDir = value;
    }

    public static void uploadFile(MultipartFile file, String fileUploadName) {
        try {
            String fullUploadName = getFullUploadName(fileUploadName);
            Path destinationPath = Path.of(fullUploadName);
            file.transferTo(destinationPath);
            log.info("file upload success : {}", fileUploadName);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
        }
    }

    public static Resource downloadFile(String fileUploadName) {
        try {
            String fullUploadName = getFullUploadName(fileUploadName);
            val resource = new UrlResource(URL_PROTOCOL + fullUploadName);

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED);
            }

            log.info("file download success : {}", fileUploadName);
            return resource;
        } catch (MalformedURLException ex) {
            throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED, ex);
        }
    }

    private static String getFullUploadName(String fileUploadName) {
        return uploadDir + fileUploadName;
    }

}
