package com.c4cometrue.mystorage.meta;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.c4cometrue.mystorage.dto.FileUploadRequest;
import com.c4cometrue.mystorage.dto.FolderContentsRequest;
import com.c4cometrue.mystorage.dto.MetaResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MetadataController {
	private final MetadataService metadataService;
	@GetMapping("/metadata")
	public ResponseEntity<List<MetaResponse>> getFolderContents(FolderContentsRequest req) {
		List<MetaResponse> res = metadataService.getFolderContents(req.folderId(), req.userId());
		return ResponseEntity.ok(res);
	}

	@PostMapping("/files")
	@ResponseStatus(HttpStatus.CREATED)
	public void uploadFile(FileUploadRequest req) {
		metadataService.uploadFile(req.multipartFile(), req.userId(), req.parentId());
	}
}
