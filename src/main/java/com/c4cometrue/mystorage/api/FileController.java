package com.c4cometrue.mystorage.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.service.FileService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

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
		FileDownloadDto.Response response = fileService.fileDownLoad(userId, fileId);
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(response.contentType()))
			.body(response.data().bytes());
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
