package com.c4cometrue.mystorage.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.exception.ErrorCd;

public class FileUtil {
    private FileUtil() {
        throw new AssertionError("should not be invoked");
    }

    /**
     * 파일 업로드를 수행한다.
     * @param multipartFile 사용자에게 받은 파일
     * @param filePath 파일이 저장될 Path
     */
    public static void uploadFile(MultipartFile multipartFile, Path filePath) {
        if (multipartFile.isEmpty()) {
            throw ErrorCd.INVALID_FILE.serviceException();
        }

        try {
            Files.copy(multipartFile.getInputStream(), filePath);
        } catch (IOException e) {
            throw ErrorCd.INTERNAL_SERVER_ERROR.serviceException();
        }
    }

    /**
     * 파일을 물리적으로 제거한다.
     * @param filePath 파일의 Path
     */
    public static void deleteFile(Path filePath) {
        if (!Files.exists(filePath)) {
            throw ErrorCd.FILE_NOT_EXIST.serviceException();
        }

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw ErrorCd.INTERNAL_SERVER_ERROR.serviceException();
        }
    }
}
