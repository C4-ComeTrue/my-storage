package com.c4cometrue.mystorage.service;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.entity.FileMetaData;
import com.c4cometrue.mystorage.exception.FileException;
import com.c4cometrue.mystorage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    private final Path storagePath;
    private final FileRepository fileRepository;

    public FileService(@Value("${storage.dir}") String root, FileRepository fileRepository) {
        this.storagePath = Paths.get(root);
        this.fileRepository = fileRepository;
    }

    public ResponseEntity<CreateFileRes> uploadFile(MultipartFile file, String username){
        if (file.isEmpty()) {
            throw new FileException(HttpStatus.BAD_REQUEST);
        }
        // UUID를 합한 파일 이름
        String fileStorageName = UUID.randomUUID() + file.getOriginalFilename();
        // 파일이 저장될 경로 (해당 경로에 저장될 파일 이름)
        Path filePath = storagePath.resolve(fileStorageName);

        try {
            Files.copy(file.getInputStream(), filePath);
            FileMetaData fileMetaData = FileMetaData.builder()
                    .fileName(file.getOriginalFilename())
                    .fileStorageName(fileStorageName)
                    .size(file.getSize())
                    .mime(file.getContentType())
                    .owner(username)
                    .build();
            // DB 저장
            fileRepository.save(fileMetaData);
            return ResponseEntity.ok(new CreateFileRes(fileMetaData));
        } catch (IOException e) {
            throw new FileException(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> deleteFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFile(fileStorageName, username);
        // 파일의 경로
        Path filePath = storagePath.resolve(fileMetaData.getFileStorageName());
        try {
            // 실제 파일과 파일 데이터 삭제
            Files.delete(filePath);
            fileRepository.delete(fileMetaData);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new FileException(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<Resource> downloadFile(String fileStorageName, String username) {
        FileMetaData fileMetaData = getFile(fileStorageName, username);

        Path filePath = storagePath.resolve(fileMetaData.getFileStorageName());
        try {
            Resource file = new UrlResource(filePath.toUri());

            if (file.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(fileMetaData.getMime()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileMetaData.getFileName() + "\"")
                        .body(file);
            } else {
                throw new FileException(HttpStatus.NOT_FOUND);
            }

        } catch (MalformedURLException e) {
            throw new FileException(HttpStatus.NOT_FOUND);
        }
    }

    public FileMetaData getFile(String fileStorageName, String username){
        FileMetaData fileMetaData = fileRepository.findByFileStorageName(fileStorageName);

        if (fileMetaData == null) {
            throw new FileException(HttpStatus.BAD_REQUEST);
        }

        if (!fileMetaData.getOwner().equals(username)) {
            throw new FileException(HttpStatus.FORBIDDEN);
        }

        return fileMetaData;
    }

}
