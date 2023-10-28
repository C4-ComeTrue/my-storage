package com.c4cometrue.mystorage.file.controller;

import com.c4cometrue.mystorage.file.dto.FileDeleteRequestDto;
import com.c4cometrue.mystorage.file.dto.FileDownloadRequestDto;
import com.c4cometrue.mystorage.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void fileUpload(
            @RequestPart(value = "file", required = false) MultipartFile multipartFile,
            @RequestPart(value = "username") String userName)
    {
        fileService.fileUpload(multipartFile, userName);
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    public void fileDownload(@RequestBody FileDownloadRequestDto dto)
    {
        fileService.fileDownload(dto.fileName(), dto.userName(), dto.downloadPath());
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void fileDelete(@RequestBody FileDeleteRequestDto dto)
    {
        fileService.fileDelete(dto.fileName(), dto.userName());
    }
}
