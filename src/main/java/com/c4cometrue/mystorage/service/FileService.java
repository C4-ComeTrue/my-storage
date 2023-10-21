package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.common.exception.BusinessException;
import com.c4cometrue.mystorage.common.exception.ErrorCode;
import com.c4cometrue.mystorage.domain.FileMetaData;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public FileUploadDto.Response fileUpload(MultipartFile file, Long userId) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        var originName = file.getOriginalFilename();
        if (isDuplicateFile(originName, userId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_FILE);
        }

        var uploadFileName = getUploadFileName(originName);
        FileUtil.uploadFile(file, uploadFileName);

        var fileMetaData = saveFileMetaData(file, userId, uploadFileName);
        return new FileUploadDto.Response(fileMetaData);
    }

    private boolean isDuplicateFile(String fileName, long userId) {
        return fileRepository.existsByFileNameAndUserId(fileName, userId);
    }

    private String getUploadFileName(String originalFileName) {
        return UUID.randomUUID() + originalFileName;
    }

    private FileMetaData saveFileMetaData(MultipartFile file, long userId, String uploadFileName) {
        var fileEntity = FileMetaData.builder()
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .uploadName(uploadFileName)
                .size(file.getSize())
                .type(file.getContentType())
                .build();

        return fileRepository.save(fileEntity);
    }

    /**
     * 파일 다운로드
     * 본인이 만든 파일이 아니면 다운이 불가
     * 파일이 다운로드 될 경로 정보도 함께 넘겨주기
     */


    /**
     * 파일 삭제
     * 파일이 존재하지 않으면 오류를 반환 + 물리적으로도 삭제
     */

}
