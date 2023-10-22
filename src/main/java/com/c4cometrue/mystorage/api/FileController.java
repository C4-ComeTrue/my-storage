package com.c4cometrue.mystorage.api;

import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/files")
@Validated
public class FileController {

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FileUploadDto.Response upload(
            @RequestPart MultipartFile file,
            @NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Valid long userId
    ) {
        return fileService.fileUpload(file, userId);
    }

    @GetMapping
    public ResponseEntity<byte[]> download(
            @NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Valid long userId,
            @NotNull(message = "파일 ID는 null이 될 수 없습니다.") @Valid long fileId
    ) {
        val response = fileService.fileDownLoad(userId, fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .body(response.data());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete(
            @NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Valid long userId,
            @NotNull(message = "파일 ID는 null이 될 수 없습니다.") @Valid long fileId
    ) {
        fileService.fileDelete(userId, fileId);
    }

}
