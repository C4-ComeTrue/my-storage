package com.c4cometrue.mystorage.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.request.FileReq;
import com.c4cometrue.mystorage.dto.request.UploadFileReq;
import com.c4cometrue.mystorage.dto.response.FileDownloadRes;
import com.c4cometrue.mystorage.dto.response.FileMetaDataRes;
import com.c4cometrue.mystorage.service.FileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    /**
     * 파일 업로드 요청
     * @param req (파일, 사용자 이름, 폴더 기본키)
     * @return {@link com.c4cometrue.mystorage.dto.response.FileMetaDataRes}
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FileMetaDataRes uploadFile(@Valid UploadFileReq req
    ) {
        return fileService.uploadFile(req.file(), req.userName(), req.folderId());
    }

    /**
     * 파일 삭제 요청
     * @param req (파일 저장소 이름, 사용자 이름, 폴더 기본키)
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@Valid FileReq req) {
        fileService.deleteFile(req.fileStorageName(), req.userName(), req.folderId());
    }

    /**
     * 파일 다운로드 요청
     * @param req (파일 저장소 이름, 사용자 이름, 폴더 기본키)
     * @return 파일(Resource)
     */
    @GetMapping
    public ResponseEntity<Resource> downloadFile(@Valid FileReq req) {
        FileDownloadRes file = fileService.downloadFile(req.fileStorageName(), req.userName(), req.folderId());
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.mime()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
            .body(file.resource());
    }
}
