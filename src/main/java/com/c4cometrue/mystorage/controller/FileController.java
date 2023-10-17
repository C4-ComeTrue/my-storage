package com.c4cometrue.mystorage.controller;

import com.c4cometrue.mystorage.dto.response.CreateFileRes;
import com.c4cometrue.mystorage.service.FileService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("")
    public ResponseEntity<CreateFileRes> uploadFile(@NotNull @RequestPart(value="file") MultipartFile file,
                                                    @NotBlank @RequestPart(value="username") String username) {
        return fileService.uploadFile(file, username);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteFile(@NotBlank @RequestParam("filename") String fileStorageName,
                                             @NotBlank @RequestParam("username") String username) {
        return fileService.deleteFile(fileStorageName, username);
    }

    @GetMapping("")
    public ResponseEntity<Resource> downloadFile(@NotBlank @RequestParam("filename") String fileStorageName,
                                                 @NotBlank @RequestParam("username") String username) {
        return fileService.downloadFile(fileStorageName, username);
    }
}
