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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FileMetaDataRes uploadFile(@Valid UploadFileReq uploadFileReq
    ) {
        return fileService.uploadFile(uploadFileReq);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@Valid FileReq fileReq) {
        fileService.deleteFile(fileReq);
    }

    @GetMapping
    public ResponseEntity<Resource> downloadFile(@Valid FileReq fileReq) {
        FileDownloadRes file = fileService.downloadFile(fileReq);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.mime()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
            .body(file.resource());
    }
}
