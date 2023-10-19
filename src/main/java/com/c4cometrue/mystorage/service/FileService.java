package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.ErrorCd;
import com.c4cometrue.mystorage.repository.FileRepository;
import com.c4cometrue.mystorage.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final StoragePathService storagePathService;

    public CreateFileRes uploadFile(MultipartFile file, String username){
        String fileStorageName = UUID.randomUUID() + file.getOriginalFilename();
        Path filePath = storagePathService.createTotalPath(fileStorageName);
        FileUtil.uploadFile(file, filePath);

        FileMetaData fileMetaData = FileMetaData.builder()
                .fileName(file.getOriginalFilename())
                .fileStorageName(fileStorageName)
                .size(file.getSize())
                .mime(file.getContentType())
                .owner(username)
                .build();

        // DB 저장
        fileRepository.save(fileMetaData);
        return new CreateFileRes(fileMetaData);
    }

    public void deleteFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFile(fileStorageName, username);
        // 파일의 경로
        Path filePath = storagePathService.createTotalPath(fileMetaData.getFileStorageName());

        FileUtil.deleteFile(filePath);
        fileRepository.delete(fileMetaData);
    }

    public Resource downloadFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFile(fileStorageName, username);
        Path filePath = storagePathService.createTotalPath(fileMetaData.getFileStorageName());

        return FileUtil.getFile(filePath);
    }

    public FileMetaData getFile(String fileStorageName, String username){
        FileMetaData fileMetaData = fileRepository.findByFileStorageName(fileStorageName);

        if (fileMetaData == null) {
            throw ErrorCd.FILE_NOT_EXIST.serviceException("[getFile] file not exist - fileStorageName: {}", fileStorageName);
        }

        if (!fileMetaData.getOwner().equals(username)) {
            throw ErrorCd.NO_PERMISSION.serviceException("[getFile] no permission - userName: {}", username);
        }

        return fileMetaData;
    }
}
