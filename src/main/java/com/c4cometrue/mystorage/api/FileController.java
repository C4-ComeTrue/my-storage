package com.c4cometrue.mystorage.api;

import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.OK)
    public FileUploadDto.Response upload(
            @RequestPart MultipartFile file,
            @NotNull(message = "유저 ID는 null이 될 수 없습니다.") @Valid Long userId
    ) {
        return fileService.fileUpload(file, userId);
    }

}
