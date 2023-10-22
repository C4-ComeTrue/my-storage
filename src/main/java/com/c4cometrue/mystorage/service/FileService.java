package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileMetaDataRepository;
import com.c4cometrue.mystorage.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final FileUtil fileUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일 업로드
     * @param file
     * @param userId
     * @return fileId, userId, uploadFilePath, fileSize
     */
    @Transactional
    public FileUploadDto.Response fileUpload(MultipartFile file, long userId) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        val originName = file.getOriginalFilename();
        if (isDuplicateFile(originName, userId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_FILE);
        }

        val uploadFilePath = getFullUploadFilePath(originName);
        fileUtil.uploadFile(file, uploadFilePath);

        val fileMetaData = saveFileMetaData(file, userId, uploadFilePath);
        return new FileUploadDto.Response(fileMetaData);
    }

    private boolean isDuplicateFile(String fileName, long userId) {
        return fileMetaDataRepository.existsByFileNameAndUserId(fileName, userId);
    }

    private String getFullUploadFilePath(String originalFileName) {
        return uploadDir + UUID.randomUUID() + originalFileName;
    }

    private FileMetaData saveFileMetaData(MultipartFile file, long userId, String uploadFileName) {
        val fileMetaData = FileMetaData.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .uploadName(uploadFileName)
                .size(file.getSize())
                .type(file.getContentType())
                .build();

        return fileMetaDataRepository.save(fileMetaData);
    }

    /**
     * 파일 다운로드
     * @param userId
     * @param fileId
     * @return fileContentByteArray, FileContentType
     */
    public FileDownloadDto.Response fileDownLoad(long userId, long fileId) {
        val fileMetaData = getFileMetaData(fileId);

        if (hasFileAccess(userId, fileMetaData.getUserId())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
        }

        val file = fileUtil.downloadFile(fileMetaData.getUploadName());
        try {
            return new FileDownloadDto.Response(
                    file.getContentAsByteArray(),
                    fileMetaData.getType()
            );
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_DOWNLOAD_FAILED,
                    String.format("failed copying byte arrays from file : %s", file.getFilename()));
        }
    }

    /**
     * 파일 삭제
     * @param fileId
     * @param userId
     */
    @Transactional
    public void fileDelete(long userId, long fileId) {
        val fileMetaData = getFileMetaData(fileId);

        if (hasFileAccess(userId, fileMetaData.getUserId())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
        }

        fileUtil.deleteFile(fileMetaData.getUploadName());
    }

    private FileMetaData getFileMetaData(long fileId) {
        return fileMetaDataRepository.findById(fileId).orElseThrow(
                () -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    }

    private boolean hasFileAccess(long userId, long fileUserId) {
        return userId != fileUserId;
    }

}
