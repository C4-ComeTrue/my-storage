package com.c4cometrue.mystorage.file;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FileDeleteRequest;
import com.c4cometrue.mystorage.dto.FileDownloadRequest;
import com.c4cometrue.mystorage.dto.FileUploadRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {
	private final FileService fileService;

	@PostMapping("/files/upload")
	public ResponseEntity<Void> uploadFile(FileUploadRequest fileUploadRequest){
		fileService.uploadFile(fileUploadRequest);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/files/delete")
	public ResponseEntity<Void> deleteFile(FileDeleteRequest request){
		fileService.deleteFile(request);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/files/download")
	public ResponseEntity<Void> downloadFile(FileDownloadRequest request){
		fileService.downloadFile(request);
		return ResponseEntity.noContent().build();
	}

}
