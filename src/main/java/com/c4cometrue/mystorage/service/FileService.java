package com.c4cometrue.mystorage.service;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.util.FileUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final StoragePathService storagePathService;
    private final ResourceLoader resourceLoader;


    /**
     * 업로드 요청한 파일 저장
     * @param file 사용자가 업로드한 파일
     * @param username 사용자 이름
     * @return 업로드된 파일 메타데이터
     */
    public CreateFileRes uploadFile(MultipartFile file, String username) {
        // 특정 사용자의 동일한 파일명 중복 처리
        if (fileRepository.findByFileNameAndUsername(file.getOriginalFilename(), username).isPresent()) {
            throw ErrorCd.DUPLICATE_FILE.serviceException();
        }

        String fileStorageName = UUID.randomUUID() + file.getOriginalFilename();
        Path filePath = storagePathService.createTotalPath(fileStorageName);
        FileUtil.uploadFile(file, filePath);

        FileMetaData fileMetaData = FileMetaData.builder()
            .fileName(file.getOriginalFilename())
            .fileStorageName(fileStorageName)
            .size(file.getSize())
            .mime(file.getContentType())
            .username(username)
            .build();

        // DB 저장
        fileRepository.save(fileMetaData);
        return new CreateFileRes(fileMetaData);
    }

    /**
     * 파일 삭제
     * @param fileStorageName 파일 로컬 저장소 이름
     * @param username 사용자 이름
     */
    public void deleteFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFileMetaData(fileStorageName, username);
        // 파일의 경로
        Path filePath = storagePathService.createTotalPath(fileMetaData.getFileStorageName());
        // 파일 DB 정보 삭제
        fileRepository.delete(fileMetaData);
        // 파일 물리적 삭제
        FileUtil.deleteFile(filePath);
    }

    /**
     * 파일 다운로드
     * @param fileStorageName 파일 로컬 저장소 이름
     * @param username 사용자 이름
     * @return 물리적 파일 정보
     */
    public Resource downloadFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFileMetaData(fileStorageName, username);
        Path filePath = storagePathService.createTotalPath(fileMetaData.getFileStorageName());
        Resource file = resourceLoader.getResource(filePath.toString());
        if (!file.exists()) {
            throw ErrorCd.FILE_NOT_EXIST.serviceException(
                "[downloadFile] file doesn't exist - fileStorageName {}", fileStorageName);
        }
        return file;
    }

    /**
     * 파일이 DB에 존재하는지 확인
     * @param fileStorageName 파일 로컬 저장소 이름
     * @param username 사용자 이름
     * @return 파일 메타 데이터
     */
    public FileMetaData getFileMetaData(String fileStorageName, String username) {
        FileMetaData fileMetaData = fileRepository.findByFileStorageName(fileStorageName)
                .orElseThrow(() -> ErrorCd.FILE_NOT_EXIST
                .serviceException("[getFileMetaData] file not exist - fileStorageName: {}", fileStorageName));

        if (!fileMetaData.getUsername().equals(username)) {
            throw ErrorCd.NO_PERMISSION
                .serviceException("[getFileMetaData] no permission - userName: {}", username);
        }

        return fileMetaData;
    }
}
