package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileMetaDataRepository;
import com.c4cometrue.mystorage.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileMetaDataRepository fileMetaDataRepository;

    @Transactional
    public FileUploadDto.Response fileUpload(MultipartFile file, long userId) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        val originName = file.getOriginalFilename();
        if (isDuplicateFile(originName, userId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_FILE);
        }

        val uploadFileName = getUploadFileName(originName);
        FileUtil.uploadFile(file, uploadFileName);

        val fileMetaData = saveFileMetaData(file, userId, uploadFileName);
        return new FileUploadDto.Response(fileMetaData);
    }

    private boolean isDuplicateFile(String fileName, long userId) {
        return fileMetaDataRepository.existsByFileNameAndUserId(fileName, userId);
    }

    private String getUploadFileName(String originalFileName) {
        return UUID.randomUUID() + originalFileName;
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

    public FileDownloadDto.Response fileDownLoad(long userId, long fileId) {
        val fileMetaData = getFileMetaData(fileId);

        if (hasFileAccess(userId, fileMetaData.getUserId())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_ACCESS);
        }

        val file = FileUtil.downloadFile(fileMetaData.getUploadName());
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

    private FileMetaData getFileMetaData(long fileId) {
        return fileMetaDataRepository.findById(fileId).orElseThrow(
                () -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
    }

    private boolean hasFileAccess(long userId, long fileUserId) {
        return userId != fileUserId;
    }

    /**
     * 파일 삭제
     * 파일이 존재하지 않으면 오류를 반환 + 물리적으로도 삭제
     */

}
