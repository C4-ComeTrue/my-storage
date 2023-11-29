package com.c4cometrue.mystorage.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.api.dto.FileDeleteDto;
import com.c4cometrue.mystorage.api.dto.FileDownloadDto;
import com.c4cometrue.mystorage.api.dto.FileUploadDto;
import com.c4cometrue.mystorage.service.FileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/files")
public class FileController {

	private final FileService fileService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FileUploadDto.Response upload(
		@RequestPart MultipartFile file, long userId, long folderId
	) {
		return fileService.fileUpload(file, userId, folderId);
	}

	@GetMapping
	public ResponseEntity<byte[]> download(
		@RequestBody @Valid FileDownloadDto.Request request
	) {
		FileDownloadDto.Response response = fileService.fileDownLoad(request.userId(), request.fileId());
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(response.contentType()))
			.body(response.data().bytes());
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	public void delete(
		@RequestBody @Valid FileDeleteDto.Request request
	) {
		fileService.fileDelete(request.userId(), request.fileId());
	}

}
