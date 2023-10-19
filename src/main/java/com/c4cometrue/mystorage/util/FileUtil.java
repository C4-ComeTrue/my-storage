package com.c4cometrue.mystorage.util;

import com.c4cometrue.mystorage.exception.ErrorCd;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    private FileUtil() {
        throw new AssertionError("should not be invoked");
    }

    /**
     * 파일 업로드를 수행한다.
     * @param multipartFile
     * @param filePath
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
     * @param filePath
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

    /**
     * 파일을 물리 디스크에서 가져온다.
     * @param filePath
     * @return
     */
    public static Resource getFile(Path filePath) {
        try {
            var file = new UrlResource(filePath.toUri());

            if (file.exists()) {
                return file;
            }

            throw ErrorCd.FILE_NOT_EXIST.serviceException();
        } catch (MalformedURLException e) {
            throw ErrorCd.FILE_NOT_EXIST.serviceException();
        }
    }
}
