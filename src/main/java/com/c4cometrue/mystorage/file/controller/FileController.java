package com.c4cometrue.mystorage.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c4cometrue.mystorage.file.dto.FileDeleteRequestDto;
import com.c4cometrue.mystorage.file.dto.FileDownloadRequestDto;
import com.c4cometrue.mystorage.file.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@PostMapping("/upload")
	@ResponseStatus(HttpStatus.CREATED)
	public void fileUpload(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
		@RequestPart(value = "username") String userName) {
		fileService.fileUpload(multipartFile, userName);
	}

	@GetMapping("/download")
	@ResponseStatus(HttpStatus.OK)
	public void fileDownload(@RequestBody FileDownloadRequestDto dto) {
		fileService.fileDownload(dto.fileName(), dto.userName(), dto.downloadPath());
	}

	@DeleteMapping("/delete")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fileDelete(@RequestBody FileDeleteRequestDto dto) {
		fileService.fileDelete(dto.fileName(), dto.userName());
	}
}
