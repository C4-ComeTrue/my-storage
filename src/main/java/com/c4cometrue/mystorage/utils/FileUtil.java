package com.c4cometrue.mystorage.utils;

import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Component
public class FileUtil {

    private FileUtil() {}

    private static String uploadDir;

    @Value("${file.upload-dir}")
    public void setUploadDir(String value) {
        uploadDir = value;
    }

    public static void uploadFile(MultipartFile file, String fileUploadName) {
        try {
            Path destinationPath = Path.of(uploadDir + fileUploadName);
            file.transferTo(destinationPath);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED, ex);
        }
    }

}
